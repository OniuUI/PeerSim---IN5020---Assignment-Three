import matplotlib.pyplot as plt
# Datasets
ShuffleK30 = [0, 0, 0, 0, 0, 0, 2, 1, 4, 9, 32, 24, 42, 76, 112, 116, 178, 191, 251, 292, 367, 390, 384, 450, 472,
              471, 454, 482, 444, 423, 393, 372, 378, 320, 302, 266, 245, 228, 192, 165, 166, 133, 114, 119, 107,
              103, 66, 79, 60, 41, 45, 42, 21, 29, 25, 23, 20, 22, 21, 17, 13, 16, 8, 9, 13, 11, 7, 15, 6, 6, 6, 4,
              7, 9, 7, 8, 1, 3, 6, 4, 3, 3, 0, 8, 1, 3, 2, 0, 2, 1, 1, 1, 3, 0, 1, 0, 0, 0, 2, 0, 0, 3, 1, 2, 0, 1,
              1, 2, 2, 0, 1, 2, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
              1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1]

RandomK30 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 8, 9, 24, 31, 50, 80, 123, 212, 269, 311, 405, 490, 574, 655,
             747, 766, 716, 708, 688, 602, 561, 426, 372, 321, 247, 175, 128, 100, 76, 44, 30, 16, 14, 6, 2, 7, 0,
             1, 2, 0, 0, 0, 0, 1]

ShuffleK50 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 12, 11, 12, 25, 20, 24, 49,
              69, 79, 105, 120, 155, 173, 224, 246, 239, 318, 329, 368, 338, 452, 397, 426, 444, 418, 434, 403, 378,
              367, 351, 324, 308, 323, 276, 272, 234, 170, 167, 125, 136, 108, 97, 79, 77, 46, 44, 45, 34, 25, 26,
              18, 4, 9, 10, 5, 8, 5, 9, 6, 3, 2, 3, 1, 3, 2, 2, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
              0, 0, 1]

RandomK50 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 3, 1, 9, 9, 13, 30,
             35, 63, 78, 121, 134, 176, 209, 260, 315, 372, 412, 444, 524, 509, 540, 546, 569, 527, 523, 494, 464,
             443, 353, 338, 279, 246, 232, 177, 126, 99, 96, 52, 53, 33, 31, 18, 12, 11, 7, 2, 2, 2, 2, 0, 1, 2]


def average_path_length(data, graphType):
    plt.plot(data)
    plt.ylabel('Nodes')
    plt.xlabel('In-degree')
    plt.legend(Legend)
    plt.yscale(graphType)
    plt.title('Average Path Length')


def average_clustering_coefficient(data, graphType):
    plt.plot(data)
    plt.ylabel('Nodes')
    plt.xlabel('In-degree')
    plt.legend(Legend)
    plt.yscale(graphType)
    plt.title('Average Clustering Coefficient')


def in_degree_distribution(data, graphType):
    plt.plot(data)
    plt.ylabel('Nodes')
    plt.xlabel('In-degree')
    plt.legend(Legend)
    plt.yscale(graphType)
    plt.title('In-Degree Distribution')


if __name__ == '__main__':
    Legend = ['Shuffle = 30', 'Random = 30', 'Shuffle = 50', 'Random = 50']

    in_degree_distribution(ShuffleK30, 'linear')
    in_degree_distribution(RandomK30, 'linear')
    in_degree_distribution(ShuffleK50, 'linear')
    in_degree_distribution(RandomK50, 'linear')

    plt.show()
