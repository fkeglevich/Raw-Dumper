cmake_minimum_required(VERSION 3.4.1)

add_library( raw-dumper SHARED 		src/main/cpp/DngNegativeJni.cpp
									src/main/cpp/ExifWriterJni.cpp
									src/main/cpp/WhiteBalanceJni.cpp)

find_library( log-lib log )

add_subdirectory(src/main/cpp/dng_sdk)

target_link_libraries( raw-dumper dng-sdk ${log-lib} )