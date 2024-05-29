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
plt.figure(figsize=(16, 8))

lr_types = {
    'exp': lambda epoch, lr_initial, decay_rate: lr_initial * np.exp(-decay_rate * epoch),
    'step': lambda epoch, lr_initial, decay_rate: lr_initial * (0.5 ** (epoch // 10))
}


@utils.memory_decorator
@utils.time_decorator
def stochastic_gradient_descent(X, y, lr_function, learning_rate=0.01, n_epochs=50, batch_size=1, decay_rate=0.1):
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
    return loss_history


results = {}
for batch_size in batch_sizes:
    for function_name in lr_types:
        utils.solve_by_name_by_solver(plt,
                                      f'Batch size {batch_size}, LR Type: {function_name}',
                                      lambda: stochastic_gradient_descent(X_b, y, lr_types[function_name],
                                                                          learning_rate=0.01, n_epochs=50,
                                                                          batch_size=batch_size, decay_rate=0.1))

plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.title('SGD Loss Reduction by Batch Size and Learning Rate Type')
plt.legend()
plt.show()
