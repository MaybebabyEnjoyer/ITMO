import numpy as np
from scipy.optimize import minimize


def f1(x):
    return (x[0] - 2) ** 2 + (x[1] - 2) ** 2


def f2(x):
    return 100 * (x[1] - x[0] ** 2) ** 2 + (1 - x[0]) ** 2


def f3(x):
    return (x[0] ** 2 + x[1] ** 2) * 0.1


def grad_f1(x):
    return np.array([2 * (x[0] - 2), 2 * (x[1] - 2)])


def grad_f2(x):
    return np.array([-400 * x[0] * (x[1] - x[0] ** 2) - 2 * (1 - x[0]), 200 * (x[1] - x[0] ** 2)])


def grad_f3(x):
    return np.array([0.2 * x[0] + 0.1 * x[1] ** 2, 0.2 * x[1] + 0.1 * x[0] ** 2])


def fn(x):
    ans = 0
    for xs in x:
        ans += (xs - 1) ** 2
    return ans


def grad_fn(x):
    ans = []
    for i in range(len(x)):
        ans.append(2 * x[i] - 2)
    return np.array(ans)


def poorly_cond_func(x, kappa):
    n = len(x)
    A = np.diag(kappa ** np.linspace(0, 1, n))
    return 0.5 * np.dot(x, np.dot(A, x))


def grad_poorly_cond_func(x, kappa):
    n = len(x)
    A = np.diag(kappa ** np.linspace(0, 1, n))
    return np.dot(A, x)


def fmulty(x):
    return (x[0] ** 2 + x[1] ** 2 - 13) ** 2 + (x[0] + x[1] ** 2 - 5) ** 2


def grad_fmulty(x):
    return np.array([4 * x[0] ** 3 + 4 * x[0] * x[1] ** 2 - 50 * x[0] + 2 * x[1] ** 2 - 10,
                     8 * x[1] ** 3 + 4 * x[0] ** 2 * x[1] - 72 * x[1] + 4 * x[0] * x[1]])


def gd(f, grad_f, x0, learning_rate=0.002, tolerance=1e-6, max_iterations=100000):
    x = np.array(x0, dtype=np.float64)
    path = [x.copy()]
    a = 1
    for i in range(max_iterations):
        grad = grad_f(x)
        x_prev = x
        step = learning_rate * grad
        if np.linalg.norm(step) > 1:
            step = step / np.linalg.norm(step)
        x = x - step * a
        path.append(x.copy())
        if np.linalg.norm(x - x_prev) < tolerance:
            break
    return x, i + 1, np.array(path)


def search_dichotomy(f, grad_f, x, tol=1e-5, max_iterations=100):
    a, b = 0, 1  # Начальный диапазон поиска размера шага
    sigma = tol / 2  # Параметр сокращения диапазона

    for _ in range(max_iterations):
        midpoint = (a + b) / 2
        left = midpoint - sigma
        right = midpoint + sigma

        f_left = f(x - left * grad_f(x))
        f_right = f(x - right * grad_f(x))

        if f_left < f_right:
            b = midpoint
        else:
            a = midpoint

        if b - a < tol:
            break

    return (a + b) / 2


def gd_ls(f, grad_f, x0, tolerance=1e-6, max_iterations=100000):
    x = np.array(x0, dtype=np.float64)
    path = [x.copy()]
    for i in range(max_iterations):
        step_size = search_dichotomy(f, grad_f, x)
        grad = grad_f(x)
        x_prev = x
        step = step_size * grad
        if np.linalg.norm(step) > 1:
            step = step / np.linalg.norm(step)
        x = x - step
        path.append(x.copy())
        if np.linalg.norm(x - x_prev) < tolerance:
            break
    return x, i + 1, np.array(path)


def coordinate_descent(f, grad, x0, tol=1e-4, max_iter=100000):
    x = np.array(x0, dtype=np.float64)
    path = [x.copy()]
    for _ in range(max_iter):
        x_prev = x.copy()
        for ind in range(len(x)):
            def f_single(var):
                temp = x.copy()
                temp[ind] = var
                return f(temp)

            def grad_f_single(var):
                temp = x.copy()
                temp[ind] = var
                d = grad(temp)
                return d[ind] if isinstance(d, np.ndarray) else d

            x_single = x[ind]
            optimal_step = search_dichotomy(f_single, grad_f_single, x_single)
            x[ind] = x_single - optimal_step * grad_f_single(x_single)

        path.append(x.copy())
        if np.linalg.norm(x - x_prev) <= tol or np.abs(f(x) - f(x_prev)) <= tol:
            break

    return x, _, np.array(path)


def nelder_mead(f, x0):
    path = [np.array(x0)]

    def callback(x):
        path.append(x.copy())

    result = minimize(f, x0, method='Nelder-Mead', callback=callback)
    return result.x, result.nit, result.nfev, np.array(path)


# Logs for first 3 funcs

initial_points = [np.array([1, 0]), np.array([-1, -1]), np.array([50, 50])]

functions = {
    'Quadratic Function 1 (f1)': (f1, grad_f1),
    'Rosenbrock Function (f2)': (f2, grad_f2),
    'Quadratic Function 2 (f3)': (f3, grad_f3),
}

results = {}

methods = {
    'Gradient Descent with Constant Step': gd,
    'Gradient Descent with Line Search': gd_ls,
    'Nelder-Meade': nelder_mead,
    'Покоординатный спуск для функции двух переменных': coordinate_descent
}

for func_name, (f, grad_f) in functions.items():
    results[(func_name, f)] = {}
    print(f"Results for {func_name}:")
    for method_name, method in methods.items():

        print(f"  {method_name}:")
        for x0 in initial_points:
            if str(x0.tolist()) not in results[(func_name, f)]:
                results[(func_name, f)][str(x0.tolist())] = []

            if 'Nelder-Meade' in method_name:
                x_final, iterations, func_evals, path = method(f, x0)
            else:
                x_final, iterations, path = method(f, grad_f, x0)
                func_evals = iterations
            print(
                f"    Initial Point {x0}: Final Point {x_final}, Iterations {iterations}, Function Evaluations {func_evals}")
            results[(func_name, f)][str(x0.tolist())].append((path, method_name))

print(results.keys())
