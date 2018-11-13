cmake_minimum_required(VERSION 3.4.1)

add_library(alipay SHARED src/main/cpp/alipay.c)

add_library(posoffline
            SHARED
            IMPORTED
           )
set_target_properties(posoffline
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libposoffline.so)


find_library( # Sets the name of the path variable.
              log-lib
              log
)


target_link_libraries(alipay posoffline)


