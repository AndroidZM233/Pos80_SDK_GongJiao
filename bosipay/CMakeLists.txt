cmake_minimum_required(VERSION 3.4.1)

add_library(bosipay SHARED src/main/cpp/bosipays.c)



add_library(QRcodeModule
            SHARED
            IMPORTED
           )
set_target_properties(QRcodeModule
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libQRcodeModule.so)
find_library( # Sets the name of the path variable.
              log-lib
              log
)


target_link_libraries(bosipay QRcodeModule)


