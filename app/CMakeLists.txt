cmake_minimum_required(VERSION 3.4.1)

add_library( dng-writer SHARED 		src/main/cpp/DngNegativeJni.cpp
									src/main/cpp/ExifWriterJni.cpp)

find_library( log-lib log )

add_subdirectory(src/main/cpp/dng_sdk)

target_link_libraries( dng-writer dng-sdk ${log-lib} )