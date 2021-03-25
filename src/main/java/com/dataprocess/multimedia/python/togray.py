import cv2
import bmp
import sys
import os


if __name__ == '__main__':
    source_file = sys.argv[1]
    target_file = sys.argv[2]
    with open(source_file, "rb") as f:
        bmpImage = bmp.BmpReader(f)
        if not os.path.isfile(target_file):
            cv2.imwrite(target_file, bmpImage.gray())