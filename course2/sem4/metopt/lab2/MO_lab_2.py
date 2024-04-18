import scipy as sp
from sympy.abc import x, y
import sympy
from scipy.optimize import minimize
from autograd import jacobian
import matplotlib.pyplot as plt
from matplotlib import cm


def get_constant_step(step_size, x_last, p, f_runnable, eps, gradient, hessian):
    return np.array([step_size, 0])


def get_dichotomy_step(step_size, x_last, p, f_runnable, eps, gradient, hessian):
    a, b = 0, 1
    sigma = eps / 2
    cnt_func = 0

    while (b - a) > eps:
        midpoint = (a + b) / 2
        left = midpoint - sigma
        right = midpoint + sigma

        xl = [x_i + left * p_i[0] for x_i, p_i in zip(x_last, p)]
        xr = [x_i + right * p_i[0] for x_i, p_i in zip(x_last, p)]
        f_left = f_runnable(xl)
        f_right = f_runnable(xr)
        cnt_func += 2

        if f_left < f_right:
            b = midpoint
        else:
            a = midpoint

    return np.array([(a + b) / 2, cnt_func])




def get_step_Wolfe_rule(step_size, x_last, p, f_runnable, eps, gradient, hessian):
    # s = p
    e1 = 0.8
    e2 = 0.2
    l = 0.8
    a = 1

    x_new = [x + a * p_i[0] for x, p_i in zip(x_last, p)]
    diff = f_runnable(x_new) - f_runnable(x_last)
    tangent = np.dot(np.squeeze(np.asarray(gradient)), np.squeeze(np.asarray(p)))
    gradient_x_new = sp.optimize.approx_fprime(x_new, f_runnable, 1e-6).reshape((2, 1))
    l1 = e1 * a * tangent
    l2 = e2 * tangent

    while ((diff - l1 > eps) and (
            np.dot(np.squeeze(np.asarray(gradient_x_new)), np.squeeze(np.asarray(p))) - l2 >= eps)):
        a = l * a
        x_new = [x + a * p_i[0] for x, p_i in zip(x_last, p)]
        diff = f_runnable(x_new) - f_runnable(x_last)
        tangent = np.dot(np.squeeze(np.asarray(gradient)), np.squeeze(np.asarray(p)))
        gradient_x_new = sp.optimize.approx_fprime(x_new, f_runnable, 1e-6).reshape((2, 1))
        l1 = e1 * a * tangent
        l2 = e2 * tangent

    return np.array([a, 7])


def gradient_descent_dichotomy(f, x0, eps, path=[], max_it=1000):
    path.append(tuple(x0))
    x_last = x0
    x_res = x0
    cnt_grad = 0
    cnt_func = 0
    cnt_iter = 0

    while True:
        if cnt_iter > max_it:
            break
        cnt_iter += 1

        cnt_grad += 1
        grad = sp.optimize.approx_fprime(x_last, f, 1e-6)

        a, b = 0, 1
        sigma = eps / 2

        while (b - a) > eps:
            midpoint = (a + b) / 2
            left = midpoint - sigma
            right = midpoint + sigma

            xl = [x_i - left * grad_i for x_i, grad_i in zip(x_last, grad)]
            xr = [x_i - right * grad_i for x_i, grad_i in zip(x_last, grad)]
            f_left = f(xl)
            f_right = f(xr)
            cnt_func += 2

            if f_left < f_right:
                b = midpoint
            else:
                a = midpoint

        step_size = (a + b) / 2
        x_new = [x_i - step_size * grad_i for x_i, grad_i in zip(x_last, grad)]
        path.append(tuple(x_new))
        if np.linalg.norm(np.array(x_new) - np.array(x_last)) < eps:
            break

        x_last = x_new

    return x_new[0], x_new[1], f(x_new), cnt_iter, cnt_grad, cnt_func, path


import numpy as np
from numpy.linalg import LinAlgError


def Newton_method(f_runnable, f_sympy, f_step, x0, eps, step_size, max_it=1000):
    path = [tuple(x0)]
    x_res = x0
    x_last = x0
    cnt_grad_and_hess = 0
    cnt_func = 0
    cnt_iter = 0
    hess = sympy.hessian(f_sympy, (x, y))

    while True:
        if cnt_iter > max_it:
            break
        cnt_iter += 1

        cnt_grad_and_hess += 1
        # Вычисление градиента
        gradient = sp.optimize.approx_fprime(x_last, f_runnable, 1e-6).reshape((2, 1))
        # Вычсиление гессиана
        hessian = hess.subs(x, x_last[0]).subs(y, x_last[1])

        if hessian.det() == 0.0:
            print("Matrix det == 0 ")
            print("Calculations continue using gradient descent.")
            print()
            return gradient_descent_dichotomy(f_runnable, x_last, eps, path)
        else:
            p = np.asarray(-((hessian ** - 1) * gradient))

        eval = f_step(step_size, x_last, p, f_runnable, eps, gradient, hessian)
        step = eval[0]
        cnt_func += eval[1]
        x_new = [x_i + step * p_i[0] for x_i, p_i in zip(x_last, p)]

        cnt_func += 2
        if abs(f_runnable(x_new) - f_runnable(x_last)) < eps:
            break

        x_last = x_res
        x_res = x_new
        path.append(tuple(x_new))

    return x_res[0], x_res[1], f_runnable(x_res), cnt_iter, cnt_grad_and_hess, cnt_func, path


def Const_step_method(f_runnable, x0, eps, f_sympy, step_size):
    return Newton_method(f_runnable, f_sympy, get_constant_step, x0, eps, step_size)


def BFGS_method(f_runnable, x0, eps):
    path = []

    result = minimize(f_runnable, x0, method='BFGS', callback=lambda x: path.append(x.copy()))
    return result.x[0], result.x[1], result.fun, result.nit, result.njev, result.nfev, path


def Nelder_Mead_method(f_runnable, x0, eps):
    path = []
    result = minimize(f_runnable, x0, method='Nelder-Mead', callback=lambda x: path.append(x.copy()))
    return result.x[0], result.x[1], result.fun, result.nit, 0, result.nfev, path


def Newton_CG_method(f_runnable, x0, eps):
    path = []
    result = minimize(f_runnable, x0, method='Newton-CG', jac=jacobian(f_runnable),
                      callback=lambda x: path.append(x.copy()))
    return result.x[0], result.x[1], result.fun, result.nit, result.nfev, result.njev, path

def Wolfe_rule_method(f_runnable, x0, eps, f_sympy):
    return Newton_method(f_runnable, f_sympy, get_step_Wolfe_rule, x0, eps, 0)


def Dichotomy_method(f_runnable, x0, eps, f_sympy):
    return Newton_method(f_runnable, f_sympy, get_dichotomy_step, x0, eps, 0)


class Method:
    def __init__(self, name, method_function, len_args):
        self.name = name
        self.method_function = method_function
        self.len_args = len_args

    def run(self, function, args):
        print("Running method " + self.name + " for " + function)
        return self.method_function(*args[:self.len_args])

    def eval(self, function_, args):
        x, y, ans, it_count, grad_ev, fun_ev, path = self.run(str(function_), args)
        print("++")
        print(f"FINAL                [{x}, {y}]")
        print(f"RESULT               {ans}")
        print(f"ITERATION            {it_count}")
        print(f"GRADIENT EVAL COUNT  {grad_ev}")
        print(f"FUNCTION EVAL COUNT  {fun_ev}")
        print("++")
        self.print(path, args[1], function_)

    def print(self, path, start_point, func_sympy):
        x, y = sympy.symbols('x y')

        x_vals = np.linspace(start_point[0] - 5, start_point[0] + 5, 400)
        y_vals = np.linspace(start_point[1] - 5, start_point[1] + 5, 400)
        X, Y = np.meshgrid(x_vals, y_vals)
        F = sympy.lambdify((x, y), func_sympy, 'numpy')
        Z = F(X, Y)

        fig, ax = plt.subplots(figsize=(8, 6))
        CS = ax.contourf(X, Y, Z, levels=50, cmap=cm.viridis)
        cbar = fig.colorbar(CS)
        cbar.ax.set_ylabel('Function value')

        points = np.array([start_point] + path)
        ax.plot(points[:, 0], points[:, 1], 'r-o', label='Optimization Path')
        ax.plot(start_point[0], start_point[1], 'bo', label='Start Point')

        ax.set_title(f"{self.name}\n{str(func_sympy)}\nStart = {start_point}")
        ax.set_xlabel('x')
        ax.set_ylabel('y')
        ax.legend()

        plt.show()
        plt.savefig("./")


def task(methods, start_point, f_runnable, f_sympy, const_step, eps):
    args = [f_runnable, start_point, eps, f_sympy, const_step]
    for method in methods:
        method.eval(f_sympy, args)


# _______________MAIN__TASK_______________

def start_main_subtask_for_function(start_point, f_runnable, f_sympy, const_step, eps):
    print("_____________________________")
    print("Starting point: " + str(start_point))
    print("Const step: ", const_step)
    print("Required accuracy : ", eps)
    task([
        Method("Nelder Mead (zero order)", Nelder_Mead_method, 3),
        Method("Gradient (dichotomy)", gradient_descent_dichotomy, 3)
    ], start_point, f_runnable, f_sympy, const_step, eps)
    print("_____________________________")


def start_main_task_for_function(start_point, f_runnable, f_sympy, const_step, eps):
    print("_____________________________")
    print("START = " + str(start_point))
    print("STEP = ", const_step)
    print("EPS = ", eps)
    task([
        Method("Constant Step Newton Method", Const_step_method, 5),
        Method("Dichotomy Step Newton Method", Dichotomy_method, 4),
        Method("Scipy Newton-CG Method", Newton_CG_method, 3),
        Method("Scipy BFGS Quasi-Newton Method",
               BFGS_method, 3)
    ], start_point, f_runnable, f_sympy, const_step, eps)
    print("_____________________________")


# ______________FIRST__TASK_______________

def start_first_task_for_function(start_point, f_runnable, f_sympy, const_step, eps):
    print("_____________________________")
    print("Starting point: " + str(start_point))
    print("Const step: ", const_step)
    print("Required accuracy : ", eps)
    task([Method("Wolfe Rule Newton Method", Wolfe_rule_method, 4),
          Method("Scipy Newton-CG Optimizer", Newton_CG_method, 3),
          Method("Dichotomy Newton Method", Dichotomy_method, 4)], start_point, f_runnable, f_sympy,
         const_step, eps)
    print("_____________________________")


# ______________SECOND__TASK______________
def start_second_task_for_function(start_point, f_runnable, f_sympy, const_step, eps):
    start_main_task_for_function(start_point, f_runnable, f_sympy, const_step, eps)


def start_main_task(functions):
    print("____________MAIN__TASK_____________")
    for fun in functions:
        fun.run(start_main_task_for_function)
    print("___________________________________")
    print("___________MAIN_SUBTASK____________")
    for fun in functions:
        fun.run(start_main_task_for_function)
    print("___________________________________")


def start_first_task(functions):
    print("____________FIRST_TASK_____________")
    for fun in functions:
        fun.run(start_first_task_for_function)
    print("___________________________________")


def start_second_task(functions):
    print("___________SECOND__TASK____________")
    for fun in functions:
        fun.run(start_second_task_for_function)
    print("___________________________________")


class Function_for_task:
    def __init__(self, fun_sympy, start, fun_, step, eps):
        self.fun_sympy = fun_sympy
        self.start = start
        self.fun_ = fun_
        self.step = step
        self.eps = eps

    def run(self, task_):
        task_(self.start, self.fun_, self.fun_sympy, self.step, self.eps)


def rosenbrock(x):
    return 100 * (x[1] - x[0] ** 2) ** 2 + (1 - x[0]) ** 2


rosenbrock_sympy = 100 * (y - x ** 2) ** 2 + (1 - x) ** 2


def himmelblau(x):
    return (x[0] ** 2 + x[1] - 11) ** 2 + (x[0] + x[1] ** 2 - 7) ** 2


himmelblau_sympy = (x ** 2 + y - 11) ** 2 + (x + y ** 2 - 7) ** 2


def function2(x):
    return -(1 / (x[0] ** 2 + 1) ** 2 + 1 / (x[1] ** 2 + 1)) ** 3


function2_sympy = -(1 / (x ** 2 + 1) ** 2 + 1 / (y ** 2 + 1)) ** 3

main_task_functions = [
    Function_for_task(rosenbrock_sympy, [-2, 1], rosenbrock, 0.1, 1e-6),
    Function_for_task(function2_sympy, [-2, 1], function2, 0.1, 1e-6),
Function_for_task(rosenbrock_sympy, [1, -2], rosenbrock, 0.1, 1e-6),
    Function_for_task(function2_sympy, [0, -2], function2, 0.1, 1e-6),
]

start_main_task(main_task_functions)

first_task_functions = [
    Function_for_task(rosenbrock_sympy, [-2, 1], rosenbrock, 0.1, 1e-6),
    Function_for_task(function2_sympy, [-2, 1], function2, 0.1, 1e-6),
Function_for_task(rosenbrock_sympy, [1, -2], rosenbrock, 0.1, 1e-6),
    Function_for_task(function2_sympy, [0, -2], function2, 0.1, 1e-6),
]

start_first_task(main_task_functions)

second_task_functions = [
    Function_for_task(himmelblau_sympy, [0, 1], himmelblau, 0.01, 1e-6)
]

start_second_task(second_task_functions)
