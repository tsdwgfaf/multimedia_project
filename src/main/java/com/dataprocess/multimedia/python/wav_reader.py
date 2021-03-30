import struct
import numpy as np
import matplotlib.pyplot as plt
import sys


class WavReader:
    def __init__(self, file):
        # RIFF区块
        self.riffId = struct.unpack('>4s', file.read(4))[0]
        self.riffSize = struct.unpack('<I', file.read(4))[0]
        self.riffType = struct.unpack('>4s', file.read(4))[0]
        # FORMAT区块
        self.formatId = struct.unpack('>4s', file.read(4))[0]
        self.formatSize = struct.unpack('<I', file.read(4))[0]
        self.formatAudioFormat = struct.unpack('<H', file.read(2))[0]
        self.formatNumChannels = struct.unpack('<H', file.read(2))[0]
        self.formatSampleRate = struct.unpack('<I', file.read(4))[0]
        self.formatByteRate = struct.unpack('<I', file.read(4))[0]
        self.formatBlockAlign = struct.unpack('<H', file.read(2))[0]
        self.formatBitsPerSample = struct.unpack('<H', file.read(2))[0]
        # DATA区块
        self.dataId = struct.unpack('>4s', file.read(4))[0]
        self.dataSize = struct.unpack('<I', file.read(4))[0]
        self.dataData = file.read()

        if self.formatBitsPerSample == 8:  # 8bits
            values = np.zeros(shape=(1, self.dataSize), dtype='uint8')  # 8位无符号
            for i in range(self.dataSize):
                values[i] = struct.unpack('<B', self.dataData[i])[0]
        elif self.formatBitsPerSample == 16:  # 16bits
            values = np.zeros(int(self.dataSize / 2), dtype='int16')
            for i in range(int(self.dataSize / 2)):
                values[i] = struct.unpack('<h', self.dataData[2 * i:2 * i + 2])[0]  # 小端序
        else:
            print("ERROR: 无法解析的采样位数")
            return
        self.x_values = np.linspace(0, float(self.dataSize) / self.formatByteRate,
                               int(self.dataSize / (self.formatNumChannels * (self.formatBitsPerSample / 8))))  # 时间轴
        if self.formatNumChannels == 1:  # 单声道
            self.y_values = values
        if self.formatNumChannels == 2:  # 双声道
            self.y_left_values, self.y_right_values = values.reshape((int(len(values) / 2), 2)).T

    def print_info(self):
        print("============================文件信息============================")
        if self.formatNumChannels == 1:
            print("声道: 单声道")
        elif self.formatNumChannels == 2:
            print("声道: 双声道")
        else:
            print("声道: 未知")

        print("采样率: " + str(self.formatSampleRate) + "Hz")
        print("采样位数: " + str(self.formatBitsPerSample))
        print("时长: " + str(int(self.dataSize / self.formatByteRate)) + "秒")
        print("文件大小: %.2fKB" % (float(self.riffSize + 8) / 1024))
        print("============================文件信息============================")

    def draw_oscillograph(self):
        if self.formatNumChannels == 1:  # 单声道
            plt.plot(self.x_values, self.y_values)
            plt.show()
        if self.formatNumChannels == 2:  # 双声道
            plt.subplot(2, 1, 1)
            plt.plot(self.x_values, self.y_left_values)
            plt.title("left")
            # plt.show()
            plt.subplot(2, 1, 2)
            plt.plot(self.x_values, self.y_right_values)
            plt.title("right")
            plt.show()

    def save(self, targetPath):
        if self.formatNumChannels == 1:  # 单声道
            plt.plot(self.x_values, self.y_values)
            plt.savefig(targetPath)
        if self.formatNumChannels == 2:  # 双声道
            plt.subplot(2, 1, 1)
            plt.plot(self.x_values, self.y_left_values)
            plt.title("left")
            plt.subplot(2, 1, 2)
            plt.plot(self.x_values, self.y_right_values)
            plt.title("right")
            plt.savefig(targetPath)


if __name__ == '__main__':
    sourceFile = sys.argv[1]
    targetFile = sys.argv[2]
    with open(sourceFile, 'rb') as f:
        wav = WavReader(f)
        wav.save(targetFile)
