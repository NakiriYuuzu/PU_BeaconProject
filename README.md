# 校園微定位服務

## 1. 使用擴充
```groovy
    //Extended
    implementation 'org.altbeacon:android-beacon-library:2.19'  //Beacon
    implementation 'com.permissionx.guolindev:permissionx:1.4.0' //Permission
    implementation 'com.airbnb.android:lottie:4.0.0' //Lottie animation
    implementation "com.android.volley:volley:1.2.1" // Volley http request
    implementation 'com.google.android.gms:play-services-maps:17.0.1' //Google map
    implementation 'com.google.android.gms:play-services-location:18.0.0' //Google location
    implementation 'com.google.code.gson:gson:2.8.6' //Gson
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0' //MPAndroidChart chart

    implementation 'com.github.bumptech.glide:glide:4.12.0'  //Glide image 用於顯示圖片
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0' //Glide image 用於顯示圖片
    implementation "androidx.fragment:fragment:1.3.6"  //Fragment

    //Fragment & btm navigation
    androidTestImplementation "androidx.navigation:navigation-testing:2.3.5"  //測試用
    implementation "androidx.navigation:navigation-dynamic-features-fragment:2.3.5" //動態載入
    implementation 'androidx.navigation:navigation-fragment:2.3.5' //Fragment
    implementation 'androidx.navigation:navigation-ui:2.3.5' //fragment navigation
```

## 2. 使用説明
1. 請打開 DefaultSetting，并且更換新的 API 連結