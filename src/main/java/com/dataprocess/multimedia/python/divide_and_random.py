import cv2
import bmp
import sys
import os


if __name__ == '__main__':
    source_file = sys.argv[1]
    target_file = sys.argv[2]
    x_blocks = int(sys.argv[3])
    y_blocks = int(sys.argv[4])
    with open(source_file, "rb") as f:
        bmpImage = bmp.BmpReader(f)
        if not os.path.isfile(target_file):
            cv2.imwrite(target_file, bmpImage.divide_and_random(x_blocks, y_blocks))
