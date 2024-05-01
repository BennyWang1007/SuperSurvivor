import os

import cv2
import numpy as np


def rotate_image(image, angle):
    image_center = tuple(np.array(image.shape[1::-1]) / 2)
    rot_mat = cv2.getRotationMatrix2D(image_center, angle, 1.0)
    result = cv2.warpAffine(image, rot_mat, image.shape[1::-1], flags=cv2.INTER_LINEAR)
    return result


if __name__ == '__main__':
    img = cv2.imread('assets/sword.png', cv2.IMREAD_UNCHANGED)
    img = cv2.resize(img, (100, 100))
    for i in range(0, 360, 3):
        rotated = rotate_image(img, i)
        cv2.imwrite(f'assets/sword_rotated_{i}.png', rotated)

