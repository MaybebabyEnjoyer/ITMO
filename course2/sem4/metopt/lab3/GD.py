import numpy as np
import matplotlib.pyplot as plt
import utils

# Генерация синтетических данных
np.random.seed(0)
X = 2 * np.random.rand(100, 1)
y = 4 + 3 * X + np.random.randn(100, 1)

# Добавление единичного столбца для учета смещения
X_b = np.c_[np.ones((100, 1)), X]
batch_sizes = [1, 10, 20, 50, 100]  # Различные размеры батчей

lr_types = {
    'exp': lambda epoch, lr_initial, decay_rate: lr_initial * np.exp(-decay_rate * epoch),
    'step': lambda epoch, lr_initial, decay_rate: lr_initial * (0.5 ** (epoch // 10)),
    'const': lambda epoch, lr_initial, decay_rate: lr_initial
}


# много запусков сделать
@utils.memory_decorator
@utils.time_decorator
def stochastic_gradient_descent(X, y, lr_function, learning_rate=0.01, n_epochs=500, batch_size=1, decay_rate=0.1,
                                eps=0.4):
    m = len(X)
    theta = np.random.randn(2, 1)
    loss_history = []
    for epoch in range(n_epochs):
        current_lr = lr_function(epoch, learning_rate, decay_rate)
        shuffled_indices = np.random.permutation(m)
        X_shuffled = X[shuffled_indices]
        y_shuffled = y[shuffled_indices]
        for i in range(0, m, batch_size):
            xi = X_shuffled[i:i + batch_size]
            yi = y_shuffled[i:i + batch_size]
            gradients = 2 / batch_size * xi.T.dot(xi.dot(theta) - yi)
            theta = theta - current_lr * gradients
        loss = np.sum((X.dot(theta) - y) ** 2) / m
        loss_history.append(loss)
        if loss <= eps:
            return loss_history, epoch

    return loss_history, n_epochs


learning_rates = [0.1, 0.05, 0.025]
decay_rates = [0.1, 0.01, 0.001]
epochs = [200, 500, 1000]
for epoch in epochs:
    print("NEW EPOCH", epoch)
    for decay_rate in decay_rates:
        print("decay_rate:", decay_rate)
        for learning_rate in learning_rates:
            print("learning_rate:", learning_rate)
            for function_name in lr_types:
                print("function_name:", function_name)
                plt.figure(figsize=(20, 10))
                for batch_size in batch_sizes:
                    utils.solve_by_name_by_solver(plt,
                                                  f'Batch size {batch_size}',
                                                  lambda: stochastic_gradient_descent(X_b, y, lr_types[function_name],
                                                                                      learning_rate=learning_rate,
                                                                                      n_epochs=epoch,
                                                                                      batch_size=batch_size,
                                                                                      decay_rate=decay_rate))

                plt.xlabel('Epoch')
                plt.ylabel('Loss')
                plt.title(f'SGD LR Type: {function_name}, '
                          f'learning rate: {learning_rate}, '
                          f'epoch: {epoch}, decay rate: {decay_rate}, ')
                plt.legend()
                plt.savefig(f"graphics_GD/SGD_{function_name}_{learning_rate}_{epoch}_{decay_rate}.png")
