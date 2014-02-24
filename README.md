Andstrap
========

A template/bootstrap Android app preconfigured with some common classes &amp; libraries

Usage
-----

<pre>
<code>
  andstrap.py [-h] [-d OUTPUT_DIR] APP_NAME PACKAGE PREFIX COMPILE_SDK_VERSION MIN_SDK_VERSION TARGET_SDK_VERSION

  Create an Android App skeleton
  
  positional arguments:
    APP_NAME              the name of the generated application
    PACKAGE               the name of the generated package
    PREFIX                prefix for import classes. Eg 'Weather' for WeatherApp
                          and WeatherContentProvider
    COMPILE_SDK_VERSION   Sdk version to use to compile the app. Eg 19
    MIN_SDK_VERSION       Minimum sdk version the app targets. Eg 14
    TARGET_SDK_VERSION    Target sdk version the app targets. Eg 19
  
  optional arguments:
    -h, --help            show this help message and exit
    -d OUTPUT_DIR, --output_directory OUTPUT_DIR
                          Output direct for the generated project
</code>
</pre>

Example:

`./andstrap.py -d /home/neo/ WeatherApp com.example.test Weather 19 14 19`
