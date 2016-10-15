#UltraIndicator

A custom indicator for ViewPager. Just adapt to six pages.

![](https://github.com/andyxialm/UltraIndicator/blob/master/art/screenshot.gif?raw=true)

### Usage
	
##### Edit your layout XML:

~~~ xml
<cn.refactor.ultraindicator.lib.UltraIndicatorView
        xmlns:uiv="http://schemas.android.com/apk/res-auto"
        android:id="@+id/ultra_indicator_view"
        android:layout_width="40dp"
        android:layout_height="40dp"
        uiv:checkedColor="@color/colorAccent"
        uiv:uncheckedColor="@color/colorPrimary"
        uiv:duration="300"/>
~~~


##### Java Code:
~~~ xml
ultraIndicatorView.setupWithViewPager(viewPager);
~~~

##### Or
~~~ xml
ultraIndicatorView.setCheckedPosition(position);
~~~

### License

    Copyright 2016 andy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.