import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from sklearn.preprocessing import PolynomialFeatures

import utils

# Генерация данных
np.random.seed(0)
X = 2 * np.random.rand(100, 1) - 1  # данные в диапазоне [-1, 1]
y = 0.5 * X ** 2 + X + 2 + np.random.randn(100, 1) * 0.1  # квадратичная зависимость с шумом

# Полиномиальные признаки
poly_features = PolynomialFeatures(degree=2, include_bias=True)
X_poly = poly_features.fit_transform(X)


# Определение модели с регуляризацией
def create_model(regularizer=None):
    return tf.keras.Sequential([
        tf.keras.layers.Dense(1, input_shape=(X_poly.shape[1],), kernel_regularizer=regularizer)
    ])


# Регуляризаторы
l1_reg = tf.keras.regularizers.l1(0.01)
l2_reg = tf.keras.regularizers.l2(0.01)
elastic_net_reg = tf.keras.regularizers.l1_l2(l1=0.01, l2=0.01)

# Создание моделей
model_l1 = create_model(l1_reg)
model_l2 = create_model(l2_reg)
model_elastic = create_model(elastic_net_reg)


# Функция обучения
@utils.memory_decorator
@utils.time_decorator
def train_model(model, X, y, epochs=500):
    model.compile(optimizer='sgd', loss='mean_squared_error')
    history = model.fit(X, y, epochs=epochs, verbose=0)
    return history.history['loss'], epochs


regularizers = {
    "l1": l1_reg,
    "l2": l2_reg,
    "elastic": elastic_net_reg
}
epochs = [100, 200, 500]
for epoch in epochs:
    print("NEW EPOCH", epoch)
    plt.figure(figsize=(20, 10))
    for regularizer_name in regularizers:
        utils.solve_by_name_by_solver(plt, regularizer_name,
                                      lambda: train_model(create_model(regularizers[regularizer_name]), X_poly, y,
                                                          epochs=epoch))
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title(f'Loss Reduction Across Different Regularizations epoch: {epoch}')
    plt.legend()
    plt.savefig(f"graphics_sub_task1/regularizations_epoch={epoch}.png")
    plt.show()
