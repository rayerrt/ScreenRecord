LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PROGUARD_ENABLED := disabled 
LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_PACKAGE_NAME := ScreenRecordDemo
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
include $(BUILD_PACKAGE)
