import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt

import utils
import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '4'  # чтобы tensorflow не писал варнинги
# Генерация синтетических данных
np.random.seed(0)
X = 2 * np.random.rand(100, 1)
y = 4 + 3 * X + np.random.randn(100, 1)

# Добавление единичного столбца для учета смещения
X_b = np.c_[np.ones((100, 1)), X].astype(np.float32)
y = y.astype(np.float32)

# Подготовка данных для TensorFlow
dataset = tf.data.Dataset.from_tensor_slices((X_b, y))
dataset = dataset.shuffle(buffer_size=100).batch(10)

# Инициализация модели
model = tf.keras.Sequential([
    tf.keras.layers.Dense(1, input_shape=(2,))
])

# Список оптимизаторов
optimizers = {
    'SGD': tf.keras.optimizers.SGD(learning_rate=0.01),
    'Momentum': tf.keras.optimizers.SGD(learning_rate=0.01, momentum=0.9),
    'Nesterov': tf.keras.optimizers.SGD(learning_rate=0.01, momentum=0.9, nesterov=True),
    'AdaGrad': tf.keras.optimizers.Adagrad(learning_rate=0.01),
    'RMSProp': tf.keras.optimizers.RMSprop(learning_rate=0.01),
    'Adam': tf.keras.optimizers.Adam(learning_rate=0.01)
}

loss_fn = tf.keras.losses.MeanSquaredError()




@utils.memory_decorator
@utils.time_decorator
def solve_by_optimizer(optimizer, epoch_count=500, eps=0.4):
    # Ресет весов перед тренировкой для оптимизатора
    weights = [np.random.randn(2, 1).astype(np.float32), np.random.randn(1).astype(np.float32)]
    model.set_weights(weights)

    loss_history = []
    for epoch in range(epoch_count):
        for step, (x_batch_train, y_batch_train) in enumerate(dataset):
            with tf.GradientTape() as tape:
                predictions = model(x_batch_train, training=True)
                loss = loss_fn(y_batch_train, predictions)
            grads = tape.gradient(loss, model.trainable_weights)
            optimizer.apply_gradients(zip(grads, model.trainable_weights))
        loss_history.append(loss.numpy())
        if loss <= eps:
            return loss_history, epoch
    return loss_history, epoch_count

epochs = [100, 250, 500]
def solve():
    for epoch in epochs:
        print("NEW EPOCH", epoch)
        plt.figure(figsize=(20, 10))
        for name in optimizers:
            utils.solve_by_name_by_solver(plt, name, lambda: solve_by_optimizer(optimizers[name], epoch))
        plt.xlabel('Epoch')
        plt.ylabel('Loss')
        plt.title(f'SGD epoch: {epoch}')
        plt.legend()
        plt.savefig(f"graphics_lib/SGD_epoch={epoch}.png")

solve()

