import struct
import numpy as np
import cv2
import random


class BmpReader:
    def __init__(self, file):
        # bmp文件头
        self.bfType = struct.unpack('<2s', file.read(2))[0]
        self.bfSize = struct.unpack('<i', file.read(4))[0]
        self.bfReserved1 = struct.unpack('<h', file.read(2))[0]
        self.bfReserved2 = struct.unpack('<h', file.read(2))[0]
        self.bfOffBits = struct.unpack('<i', file.read(4))[0]

        # 位图信息头
        self.biSize = struct.unpack('<i', file.read(4))[0]
        self.biWidth = struct.unpack('<i', file.read(4))[0]
        self.biHeight = struct.unpack('<i', file.read(4))[0]
        self.biPlanes = struct.unpack('<h', file.read(2))[0]
        self.biBitCount = struct.unpack('<h', file.read(2))[0]
        self.biCompression = struct.unpack('<i', file.read(4))[0]
        self.biSizeImage = struct.unpack('<i', file.read(4))[0]
        self.biXPelsPerMeter = struct.unpack('<i', file.read(4))[0]
        self.biYPelsPerMeter = struct.unpack('<i', file.read(4))[0]
        self.biClrUsed = struct.unpack('<i', file.read(4))[0]
        self.biClrImportant = struct.unpack('<i', file.read(4))[0]

        # 图像信息
        self.bmpData = file.read()

        # R,G,B
        step = int(self.biBitCount / 8)
        # opencv处理图像时，矩阵中的数据类型为'uint8'
        self.R = np.zeros((abs(self.biHeight), self.biWidth), dtype='uint8')
        self.G = np.zeros((abs(self.biHeight), self.biWidth), dtype='uint8')
        self.B = np.zeros((abs(self.biHeight), self.biWidth), dtype='uint8')

        # biHeight为正时，需要处理图片颠倒问题
        if self.biHeight > 0:
            for i in range(abs(self.biHeight)):
                for j in range(self.biWidth):
                    self.B[abs(self.biHeight) - 1 - i][j] = self.bmpData[self.biWidth * i * step + j * step]
                    self.G[abs(self.biHeight) - 1 - i][j] = self.bmpData[self.biWidth * i * step + j * step + 1]
                    self.R[abs(self.biHeight) - 1 - i][j] = self.bmpData[self.biWidth * i * step + j * step + 2]
        else:
            for i in range(abs(self.biHeight)):
                for j in range(self.biWidth):
                    self.B[i][j] = self.bmpData[self.biWidth * i * step + j * step]
                    self.G[i][j] = self.bmpData[self.biWidth * i * step + j * step + 1]
                    self.R[i][j] = self.bmpData[self.biWidth * i * step + j * step + 2]
        self.mergedImp = cv2.merge([self.B, self.G, self.R])

    def print_info(self):
        print("文件大小: %.2f KB" % (float(self.bfSize) / 1024))
        print("尺寸: " + str(self.biWidth) + "×" + str(abs(self.biHeight)))
        print("比特: " + str(self.biBitCount))

    def show(self):
        cv2.imshow("picture", self.mergedImp)

    def divide_and_random(self, x_blocks, y_blocks):
        """
        分块并打乱，展示处理后的图像
        :param x_blocks: 水平分块数
        :param y_blocks: 垂直分块数
        """
        x_size = int(self.biWidth / x_blocks)
        y_size = int(abs(self.biHeight) / y_blocks)
        if x_blocks > self.biWidth or y_blocks > abs(self.biHeight):
            print("ERROR: 分块数大于像素数，操作失败")
        blocks = np.zeros((y_size, x_size))
        # 处理无法整除的快
        left_blocks_bottom = []
        for i in range(x_blocks):
            left_blocks_bottom.append(self.mergedImp[y_size * y_blocks:, i * x_size:(i + 1) * x_size])
        random.shuffle(left_blocks_bottom)
        left_blocks_bottom_result = np.zeros((abs(self.biHeight) - y_size * y_blocks, 0, 3))
        for i in range(x_blocks):
            a = left_blocks_bottom[i]
            left_blocks_bottom_result = np.hstack([left_blocks_bottom_result, left_blocks_bottom[i]])
        left_blocks_bottom_result = np.hstack([left_blocks_bottom_result, self.mergedImp[y_size * y_blocks:, x_size * x_blocks:]])  # 右下角剩余的一快

        left_blocks_right = []
        for i in range(y_blocks):
            left_blocks_right.append(self.mergedImp[i * y_size:(i + 1) * y_size, x_size * x_blocks:])
        random.shuffle(left_blocks_right)
        left_blocks_right_result = np.zeros((0, self.biWidth - x_size * x_blocks, 3))
        for i in range(y_blocks):
            left_blocks_right_result = np.vstack([left_blocks_right_result, left_blocks_right[i]])

        # 处理能够整除的块
        center_blocks = []
        for i in range(y_blocks):
            for j in range(x_blocks):
                center_blocks.append(self.mergedImp[i * y_size:(i + 1) * y_size, j * x_size:(j + 1) * x_size])
        random.shuffle(center_blocks)
        center_blocks = np.array(center_blocks).reshape((y_blocks, x_blocks, y_size, x_size, 3))
        center_blocks_result = np.zeros((0, x_blocks * x_size, 3))
        for i in range(center_blocks.shape[0]):
            center_blocks_line = np.zeros((y_size, 0, 3))
            for j in range(center_blocks.shape[1]):
                a = center_blocks[i][j]
                center_blocks_line = np.hstack([center_blocks_line, center_blocks[i][j]])
            center_blocks_result = np.vstack([center_blocks_result, center_blocks_line])

        # 合并各分块
        result_bmp = np.vstack([left_blocks_bottom_result, np.hstack([left_blocks_right_result, center_blocks_result])])
        result_bmp.astype(np.uint8)
        result_bmp_uint8 = np.zeros((result_bmp.shape[0], result_bmp.shape[1], result_bmp.shape[2]), dtype='uint8')
        for i in range(result_bmp.shape[0]):
            for j in range(result_bmp.shape[1]):
                for k in range(result_bmp.shape[2]):
                    result_bmp_uint8[i][j][k] = np.uint8(result_bmp[i][j][k])
        # cv2.imshow("divide_and_random", result_bmp_uint8)
        return result_bmp_uint8

    def gray(self):
        gray_bmp = np.zeros((abs(self.biHeight), self.biWidth), dtype='uint8')
        for i in range(abs(self.biHeight)):
            for j in range(self.biWidth):
                gray_bmp[i][j] = np.uint8((self.mergedImp[i][j][0] / 3 + self.mergedImp[i][j][1] / 3 + self.mergedImp[i][j][2] / 3))
        # cv2.imshow("gray", gray_bmp)
        return gray_bmp

    def red(self):
        red_img = self.mergedImp.copy()
        red_img[:, :, 0] = 0
        red_img[:, :, 1] = 0
        # cv2.imshow("red", red_img)
        return red_img

    def blue(self):
        blue_img = self.mergedImp.copy()
        blue_img[:, :, 1] = 0
        blue_img[:, :, 2] = 0
        # cv2.imshow("blue", blue_img)
        return blue_img

    def green(self):
        green_img = self.mergedImp.copy()
        green_img[:, :, 0] = 0
        green_img[:, :, 2] = 0
        # cv2.imshow("green", green_img)
        return green_img
