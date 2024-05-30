import time
import tracemalloc


def print_loss_graphic(plt, name, loss_history, epoch, duration, memory_used):
    print("epoch:", epoch)
    label = f'{name} (Time: {duration:.5f}s, Mem: {memory_used / 10 ** 6:.3f}MB, Epoch:{epoch})'
    plt.plot(loss_history, label=label)
    print(label)
    print("last loss:", loss_history[-1])


def solve_by_name_by_solver(plt, name, solver):
    print_loss_graphic(plt, name, *solver())


def time_decorator(fun):
    def time_wrapper(*args, **kwargs):
        start_time = time.time()  # Начинаем отслеживать время
        return *fun(*args, **kwargs), time.time() - start_time

    return time_wrapper


def memory_decorator(fun):
    def memory_wrapper(*args, **kwargs):
        tracemalloc.start()  # Начинаем отслеживать память
        res = fun(*args, **kwargs)
        _, peak = tracemalloc.get_traced_memory()  # Пиковое использование памяти
        tracemalloc.stop()  # Остановка отслеживания памяти
        return *res, peak

    return memory_wrapper
