#!/usr/bin/python

import argparse
import sys
import os
import shutil
import json
import urllib
import zipfile
from os.path import join

HOLO_COLORS_TEMPLATE_URL = 'http://android-holo-colors.com/generate_all.php?origin=web&color=%s&holo=light_dark_action_bar&name=%s&kitkat=1&minsdk=holo&compat=compat&edittext=true&text_handle=true&autocomplete=true&button=true&checkbox=true&radio=true&progressbar=true&list=true&fastscroll=true&switchjb=true'

def generate_app_drawer_enum_code(item_list):
	enum_code = ''
	for index, (name, is_major) in enumerate(item_list):
		enum_code += '%s(%s)' % (name, is_major)
		if index == len(item_list) - 1:
			enum_code += ';'
		else :
			enum_code += ',\n\t'

	return enum_code

def generate_app_drawer_switch_code(item_list):
	switch_code = ''
	for (name, is_major) in item_list:
		switch_code += '\tcase %s:\n\t\t' % (name)
		switch_code += 'break;\n'

	return switch_code

def merge_holo_generator_assets(color, theme_name):
	zip_name = '/tmp/holo_colors.zip'
	unzip_folder = '/tmp'
	unzip_target = unzip_folder + '/res'

	if os.path.isfile(zip_name):
		os.remove(zip_name)

	url = HOLO_COLORS_TEMPLATE_URL % (color, theme_name)
	urllib.urlretrieve(url, zip_name)

	if os.path.isfile(unzip_target):
		shutil.rmtree(unzip_target)
	with zipfile.ZipFile(zip_name) as zf:
		zf.extractall(unzip_folder)

	if os.path.isfile(zip_name):
		os.remove(zip_name)

	return unzip_target

class Colors:
	def __init__(self):
		self.primary = '#FF0000'
		self.secondary = '#00FF00'

class Config:
	def __init__(self, config_json):
		self.app_name = config_json.get('app_name')
		self.app_prefix = config_json.get('app_prefix')
		self.package_name = config_json.get('package_name')
		self.compile_sdk = config_json.get('compile_sdk')
		self.target_sdk = config_json.get('target_sdk')
		self.min_sdk = config_json.get('min_sdk')

		if 'app_drawer' in config_json:
			drawer_items_array = config_json.get('app_drawer')
			self.app_drawer = [
				(item.get('name').replace(' ', '_').upper(), item.get('is_major')) for item in drawer_items_array
			]

		self.colors = Colors()
		if 'colors' in config_json:
			colors_json = config_json.get('colors')
			if 'primary' in colors_json:
				self.colors.primary = colors_json.get('primary')
			if 'secondary' in colors_json:
				self.colors.secondary = colors_json.get('secondary')


class TemplateWriter():
	def __init__(self, config, output_folder):
		self.config = config
		self.output_folder = output_folder

		self.replacements = {
			'{app_name}' : config.app_name,
			'{package_name}' : config.package_name,
			'{app_class_prefix}' : config.app_prefix,
			'{compile_sdk_version}' : config.compile_sdk,
	        '{min_sdk_version}' : config.min_sdk,
	        '{target_sdk_version}' : config.target_sdk,
	        '{color_primary}' : config.colors.primary,
	        '{color_secondary}' : config.colors.secondary,
	        '{app_drawer_enum_code}' : "" if config.app_drawer is None else generate_app_drawer_enum_code(config.app_drawer),
	        '{app_drawer_switch_code}' : "" if config.app_drawer is None else generate_app_drawer_switch_code(config.app_drawer)
		}

	def __create_output_folder(self):
		self.__create_folder(self.output_folder)

	def __create_readme(self):
		f = open(join(self.output_folder, 'README.md'), 'w')
		f.write(self.config.app_name + '\n=========')
		f.close()

	def __create_folder_structure(self):
		self.__create_folder(join(self.output_folder, 'libs'))

		base_app_folder = self.app_base_folder()

		base_test_folder = join(base_app_folder, 'src/androidTest/java')
		base_source_folder = join(base_app_folder, 'src/main/java')
		base_res_folder = join(base_app_folder, 'src/main/res')

		packge_path = self.config.package_name.split('.')

		package_folder = base_source_folder
		release_package_folder = join(base_app_folder, 'src/release/java')
		debug_package_folder = join(base_app_folder, 'src/debug/java')
		test_package_folder = base_test_folder 
		for folder in packge_path:
			package_folder = join(package_folder, folder)
			release_package_folder = join(release_package_folder, folder)
			debug_package_folder = join(debug_package_folder, folder)
			test_package_folder = join(test_package_folder, folder)


		self.__create_folder(test_package_folder)
		self.__create_folder(package_folder)
		self.__create_folder(debug_package_folder)
		self.__create_folder(release_package_folder)
		self.__create_folder(base_res_folder)

		self.__create_folder(join(base_app_folder, 'src/debug/res/values'))
		self.__create_folder(join(base_app_folder, 'src/release/res/values'))

	def __copy_source_files(self):
		files = []

		base_app_folder = self.app_base_folder()
		base_res_folder = join(base_app_folder, 'src/main/res/')
		base_source_folder = join(base_app_folder, 'src/main/java/')

		package_folder = base_source_folder
		packge_path = self.config.package_name.split('.')
		for folder in packge_path:
			package_folder = join(package_folder, folder)

		for root, dirs, files in os.walk('template/'):
			for d in dirs:
				if d == 'res_folders':
					self.copytree(join(root, d), base_res_folder)
				elif d == 'source_folders':
					self.copytree(join(root, d), package_folder)
					
			for f in files:
				filepath = join(root, f)
				if filepath in ['template/.gitignore', 'template/build.gradle', 'template/settings.gradle']:
					self.__copy_files(self.output_folder, {f})
				elif filepath == 'template/App/build.gradle':
					shutil.copyfile(filepath, join(base_app_folder, f))
				elif filepath == 'template/App/gradle.properties':
					shutil.copyfile(filepath, join(base_app_folder, f))
				elif filepath == 'template/App/AndroidManifest.xml':
					shutil.copyfile(filepath, join(join(base_app_folder, 'src/main'), f))
				elif filepath == 'template/App/release_strings.xml':
					shutil.copyfile(filepath, join(base_app_folder, 'src/release/res/values/strings.xml'))
				elif filepath == 'template/App/debug_strings.xml':
					shutil.copyfile(filepath, join(base_app_folder, 'src/debug/res/values/strings.xml'))

		#Rename android.app.Application Subclass
		os.rename(join(package_folder, 'App.java'), join(package_folder, self.config.app_prefix + 'App.java'))

		#Rename our ContentProvider
		data_folder = join(package_folder, 'data')
		os.rename(join(data_folder, 'AppContentProvider.java'), join(data_folder, self.config.app_prefix + 'ContentProvider.java'))



	def __create_folder(self, folder_path):
		if not os.path.exists(folder_path):
			print 'Creating folder ' + folder_path
			os.makedirs(folder_path)

	def __run_replacement(self):
		for root, dirs, files in os.walk(self.output_folder):
			for filename in files:
				fname = join(root, filename)
				contents = open(fname).read()
				out = open(fname, 'w')
				for i in self.replacements.keys():
					contents = contents.replace(i, str(self.replacements[i]))
				out.write(contents)
				out.close()

	def __copy_files(self, output_base, files={}):
		for f in files:
			shutil.copyfile('template/' + f, join(output_base, f))

	def copytree(self, src, dst, symlinks=False, ignore=None):
	    for item in os.listdir(src):
	        s = os.path.join(src, item)
	        d = os.path.join(dst, item)
	        if os.path.isdir(s):
	            shutil.copytree(s, d, symlinks, ignore)
	        else:
	            shutil.copy2(s, d)

	def app_base_folder(self):
		return join(self.output_folder, 'app')

	def create(self):
		self.__create_output_folder()
		self.__create_readme()
		self.__create_folder_structure()
		self.__copy_source_files()
		self.__run_replacement()

def generate_holo_colors(template, config):
	holo_colors_folder = merge_holo_generator_assets(config.colors.primary.replace('#', ''), config.app_name.lower())
	if holo_colors_folder:
		base_folder = template.app_base_folder()
		base_res_folder = join(base_folder, 'src/main/res')

		resources = {}
		for root, dirs, files in os.walk(holo_colors_folder):
			for d in dirs:
				if d.startswith('drawable') or d.startswith('values'):
					folderpath = join(root, d)
					print folderpath
					template.copytree(folderpath, join(base_res_folder, d))

		shutil.rmtree(holo_colors_folder)

def create_app(config, output_folder = None):
	if output_folder is None:
		output_folder = app_name + 'Project'

	c = Config(config)
	template = TemplateWriter(c, output_folder)
	template.create()

	generate_holo_colors(template, c)


def main():
	parser = argparse.ArgumentParser(description='Create an Android App skeleton')

	parser.add_argument('config_file', nargs='?', type=argparse.FileType('r'), default=sys.stdin, help="App config file")

	# parser.add_argument('app_name', metavar='APP_NAME', help='the name of the generated application')
	# parser.add_argument('package_name', metavar='PACKAGE', help='the name of the generated package')
	# parser.add_argument('app_class_prefix', metavar='PREFIX', help="prefix for import classes. Eg 'Weather' for WeatherApp and WeatherContentProvider")
	# parser.add_argument('compile_sdk_version', metavar='COMPILE_SDK_VERSION', type=int, help="Sdk version to use to compile the app. Eg 19")
	# parser.add_argument('min_sdk_version', metavar='MIN_SDK_VERSION', type=int, help="Minimum sdk version the app targets. Eg 14")
	# parser.add_argument('target_sdk_version', metavar='TARGET_SDK_VERSION', type=int, help="Target sdk version the app targets. Eg 19")
	parser.add_argument('-d', '--output_directory', metavar='OUTPUT_DIR', help="Output direct for the generated project")

	args = parser.parse_args()
	config = json.loads(args.config_file.read())
	create_app(config, args.output_directory)
	

if __name__ == '__main__':
	main()


