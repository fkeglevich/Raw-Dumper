cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(src/main/cpp/tiff/)

add_library( tiff-writer SHARED src/main/cpp/TiffWriterJni.cpp)

find_library( log-lib log )

target_link_libraries( tiff-writer tiff ${log-lib} )