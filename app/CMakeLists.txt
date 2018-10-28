cmake_minimum_required(VERSION 3.4.1)

add_library( tiff SHARED            src/main/cpp/tiff/libtiff/tif_dirread.c
                                	src/main/cpp/tiff/libtiff/tif_zip.c  
                                	src/main/cpp/tiff/libtiff/tif_flush.c  
                                	src/main/cpp/tiff/libtiff/tif_next.c  
                                	src/main/cpp/tiff/libtiff/tif_ojpeg.c  
                                	src/main/cpp/tiff/libtiff/tif_dirwrite.c  
                                	src/main/cpp/tiff/libtiff/tif_dirinfo.c  
                                	src/main/cpp/tiff/libtiff/tif_dir.c  
                                	src/main/cpp/tiff/libtiff/tif_compress.c  
                                	src/main/cpp/tiff/libtiff/tif_close.c  
                                	src/main/cpp/tiff/libtiff/tif_tile.c  
                                	src/main/cpp/tiff/libtiff/tif_open.c  
                                	src/main/cpp/tiff/libtiff/tif_getimage.c  
                                	src/main/cpp/tiff/libtiff/tif_pixarlog.c  
                                	src/main/cpp/tiff/libtiff/tif_warning.c  
                                	src/main/cpp/tiff/libtiff/tif_dumpmode.c  
                                	src/main/cpp/tiff/libtiff/tif_jpeg.c  
                                	src/main/cpp/tiff/libtiff/tif_jbig.c  
                                	src/main/cpp/tiff/libtiff/tif_predict.c  
                                	src/main/cpp/tiff/libtiff/mkg3states.c  
                                	src/main/cpp/tiff/libtiff/tif_write.c  
                                	src/main/cpp/tiff/libtiff/tif_error.c  
                                	src/main/cpp/tiff/libtiff/tif_version.c  
                                	src/main/cpp/tiff/libtiff/tif_print.c  
                                	src/main/cpp/tiff/libtiff/tif_color.c  
                                	src/main/cpp/tiff/libtiff/tif_read.c  
                                	src/main/cpp/tiff/libtiff/tif_extension.c  
                                	src/main/cpp/tiff/libtiff/tif_thunder.c  
                                	src/main/cpp/tiff/libtiff/tif_lzw.c  
                                	src/main/cpp/tiff/libtiff/tif_fax3.c  
                                	src/main/cpp/tiff/libtiff/tif_luv.c  
                                	src/main/cpp/tiff/libtiff/tif_codec.c  
                                	src/main/cpp/tiff/libtiff/tif_unix.c  
                                	src/main/cpp/tiff/libtiff/tif_packbits.c  
                                	src/main/cpp/tiff/libtiff/tif_aux.c  
                                	src/main/cpp/tiff/libtiff/tif_fax3sm.c  
                                	src/main/cpp/tiff/libtiff/tif_swab.c  
                                	src/main/cpp/tiff/libtiff/tif_strip.c
                                	src/main/cpp/tiff/port/lfind.c)

add_library( tiff-writer SHARED src/main/cpp/TiffWriterJni.cpp)

find_library( log-lib log )

add_subdirectory(src/main/cpp/dng_sdk)

target_link_libraries( tiff-writer tiff dng-sdk ${log-lib} )