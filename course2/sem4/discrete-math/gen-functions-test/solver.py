"""
@Authors: Arman Tovmasian, Ivan Grishin, Shirokih Nikolai

Sample usage of file for GF test:

Task 1:
div_fast([coefficients of numerator], [coefficients of denominator])
i.e. div_fast([1, 1, -1], [1, 0, 0, 1])

Task 2:
Let f = x*a_{n-3} + y*a_{n-2} + z*a_{n-1}
rec([[-1, z], [-2, y], [-3, x]], [starting values])

i.e.
a_n = 3*a_{n-1} + 6*a_{n-2} - 8*a_{n-3}
a_0 = 1, a_1 = 3, a_2 = 15
rec([[-1, 3], [-2, 6], [-3, -8]], [1, 3, 15])

Task 3:
Same as 2nd task, but use asymptotic_by_rec function instead of rec

Task 4:
Create Combinatorics object and fill it with list of Combinatorics.CombObject
Then use composition method to get the generating function, and pass the list of combinatorial classes

i.e.
c = Combinatorics([Combinatorics.CombObject(weight=1, quantity=3)])
c.composition([Combinatorics.Comb.Seq_plus.value, Combinatorics.Comb.Set_plus.value])

Task 5:
Create FormalProduct object and pass the list of inequalities coefficients
Then use solve method to get the degree of the generating function

i.e.
x1 >= 0
x2 >= x1
x3 >= x1
x4 >= 2*x1 + x2
f = FormalProduct([[0, 0, 0, 0], [1, 0, 0, 0], [1, 0, 0, 0], [2, 1, 0, 0]])
"""


from collections import Counter
from typing import Optional, Generator

import sympy
from IPython import display
from sympy import *
from enum import Enum
from dataclasses import dataclass

t = sympy.symbols('t')


class FormalProduct:
    def __init__(self, args: list[list[int]]) -> None:
        """
        Initialize FPS's coefficients

        :param args: coeffs of the inequalities

        Sample usage:

        x1 >= 0

        x2 >= x1

        x3 >= x1

        x4 >= 2*x1 + x2

        f = FormalProduct([[0, 0, 0, 0], [1, 0, 0, 0], [1, 0, 0, 0], [2, 1, 0, 0]])

        """
        self.args = args
        self.count: list[int] = [1] * 4

    def solve(self) -> tuple[int, ...]:
        """
        Multiply all formal power series and get the degree of the generating function

        :return: deg = Degree of GF, max root power - 1, max root
        """
        Q: Poly | int = 1
        for item in reversed(self.args):
            for i in range(len(item)):
                self.count[i] += (item[i] * self.count[self.args.index(item)])
            print(Q)
            Q *= 1 - t**(self.count[self.args.index(item)])
        return (int(degree(Q)),
                Counter(Q.as_poly().all_roots()).most_common(1)[0][1] - 1,
                int(Counter(Q.as_poly().all_roots()).most_common(1)[0][0]))


class Combinatorics:
    @dataclass
    class CombObject:
        weight: int
        quantity: int

    def __init__(self, objects: list[CombObject]) -> None:
        self.X: Poly | int = 0

        for obj in objects:
            self.X += obj.quantity * (t ** obj.weight)

        self.coeffs = self.X.as_poly().all_coeffs()
        self.coeffs.reverse()

    class Comb(Enum):
        Set: str = "Set"
        PSet: str = "PSet"
        Seq: str = "Seq"
        MSet: str = "MSet"
        Set_plus: str = "Set+"
        PSet_plus: str = "PSet+"
        Seq_plus: str = "Seq+"
        MSet_plus: str = "MSet+"

    def seq(self) -> Poly:
        return 1 / (1 - self.X)

    def seq_plus(self) -> Poly:
        return self.seq() - 1

    def seq_pow_k(self, k: int) -> Poly:
        return self.X ** k

    def set(self) -> Poly:
        return prod([(1 + (t ** i))**self.coeffs[i] for i in range(1, len(self.coeffs))])

    def pset(self) -> Poly:
        return self.set()

    def pset_plus(self) -> Poly:
        return self.set_plus()

    def set_plus(self) -> Poly:
        return self.set() - 1

    def mset(self) -> Poly:
        return prod([(1 / (1 - (t ** i))**self.coeffs[i]) for i in range(1, len(self.coeffs))])

    def mset_plus(self) -> Poly:
        return self.mset() - 1

    def composition(self, operations: list[str]) -> Poly:
        """
        Compose the combinatorial classes

        :param operations: list of combinatorial classes ordered by their composition

        (i.e. B = Set(MSet(X)) -> operations = ["Set", "MSet"])

        :return: composition of the combinatorial classes interpreted as generating function
        """
        for op in reversed(operations):
            print(op, self.X)
            match op:
                case "Set":
                    self.X = self.set()
                case "PSet":
                    self.X = self.pset()
                case "Seq":
                    self.X = self.seq()
                case "MSet":
                    self.X = self.mset()
                case "Set+":
                    self.X = self.set_plus()
                case "PSet+":
                    self.X = self.pset_plus()
                case "Seq+":
                    self.X = self.seq_plus()
                case "MSet+":
                    self.X = self.mset_plus()
        return simplify(expand(simplify(self.X)))


def div_fast(a: list[int], b: list[int], limit: int = 12) -> Generator[int, None, None]:
    """
    Get coefficients of the polynomial division (generating function)

    Sample usage:
    for ind, item in enumerate(div_fast([1, 1, -1], [1, 0, 0, 1])):
        print(f'f_{ind} = {item}')

    :param a: numerator coefficients
    :param b: denominator coefficients
    :param limit: number of coefficients to calculate
    :return: coefficients of the polynomial division
    """
    c = [a[0] / b[0]]
    yield c[0]
    for n in range(1, max(limit, len(a), len(b))):
        c_n = a[n] if n < len(a) else 0
        for k in range(n):
            b_nk = (b[n - k] if n - k < len(b) else 0)
            c_n -= c[k] * b_nk
        c.append(c_n / b[0])
        yield c[-1]


def div(a: list[list[int]], b: Optional[list[int]] = None, limit: int = 12, show: bool = True):
    if b is None:
        a, b = a.as_numer_denom()
        if show:
            display.display(a.expand() / b.expand())
    if not isinstance(a, list):
        try:
            a = a.as_poly().all_coeffs()[::-1]
        except Exception:
            a = [int(a)]
    if not isinstance(b, list):
        try:
            b = b.as_poly().all_coeffs()[::-1]
        except Exception:
            b = [int(b)]

    return list(div_fast(a, b, limit=limit))


def diff(a, b):
    return [x - y for x, y in zip(a, b)]


def ad(a, b):
    return [x * y for x, y in zip(a, b)]


def ad_div(a, b):
    return [x / y for x, y in zip(a, b)]


def rec(arr: list[list[int]], f0: list[int]) -> Poly:
    """
    Get generating function of the recursive sequence

    :param arr: [[recursive index, recursive coefficient], ...]
    :param f0: [recursive values]
    :return:
    """
    Q = 1
    for n, c in arr:
        n = -n
        Q -= c * t ** n
    P = 0
    arr2 = [(0, -1)] + arr
    m = max((-n for n, c in arr2))
    for i in range(0, m):
        k = 0
        for n, c in arr2:
            pw = i - -n
            if pw >= 0:
                k -= c * sympy.Symbol(f'f{pw}')
        P += k * t ** i

    for i, f in enumerate(f0):
        P = P.subs(sympy.Symbol(f'f{i}'), f)

    a = P / Q
    # assert div(a, show=False, limit=1000) == check_recursive(arr, f0, limit=1000)
    return a


def asymptotic_by_rec(arr: list[list[int]], f0: list[int]) -> Poly:
    """
    Get asymptotic of the recursive sequence

    :param arr: [[recursive index, recursive coefficient], ...]
    :param f0: [recursive values]
    :return: asymptotic of the recursive sequence
    """
    gf = rec(arr, f0)
    roots = Counter([abs(1/item) for item in gf.args[0].as_poly().all_roots()])
    max_root = max(roots)
    return t**(roots[max_root] - 1) * max_root**t


def asymptotic_by_gf(Q: Poly) -> Poly:
    """
    Get asymptotic of the generating function

    :param Q: generating function
    :return: asymptotic of the generating function
    """
    roots = Counter([abs(1/item) for item in Q.as_poly().all_roots()])
    max_root = max(roots)
    return t**(roots[max_root] - 1) * max_root**t


if __name__ == '__main__':
    # r = rec([[-1, 3], [-2, 6], [-3, -8]], [1, 3, 15])
    # print(asymptotic_by_rec([[-1, 3], [-2, 6], [-3, -8]], [1, 3, 15]))
    # print(asymptotic_by_gf(1 - 2*t - t**2 + 2*t**3))
    # r = rec([[-1, 1], [-2, -2], [-3, 2]], [-3, 1, 3])
    # print(asymptotic_by_rec([[-1, 2], [-2, 5], [-3, -6]], [1, 2, 9]))
    # print(c.composition([Combinatorics.Comb.Seq.value, Combinatorics.Comb.MSet_plus.value]))
    # c = Combinatorics([Combinatorics.CombObject(weight=1, quantity=3)])
    # print(c.composition([Combinatorics.Comb.Seq_plus.value, Combinatorics.Comb.Set_plus.value]))
    # f = FormalProduct([[0, 0, 0, 0], [2, 0, 0, 0], [1, 0, 0, 0], [0, 3, 1, 0]])
    # f = FormalProduct([[0, 0, 0, 0], [1, 0, 0, 0], [1, 0, 0, 0], [2, 1, 0, 0]])
    # print(f.solve())
    print(rec([[-1, 2], [-2, 1]], [1, 1]))
