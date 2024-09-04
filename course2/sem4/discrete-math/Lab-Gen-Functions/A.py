class Polynomial:
    MOD = 998244353

    def __init__(self, coefficients: list[int] | None = None):
        if coefficients is None:
            coefficients = []
        self.coefs = coefficients

    def resize(self, size: int) -> None:
        if size > len(self.coefs):
            self.coefs.extend([0] * (size - len(self.coefs)))
        elif size < len(self.coefs):
            self.coefs = self.coefs[:size]

    def shrink(self) -> None:
        while self.coefs and self.coefs[-1] == 0:
            self.coefs.pop()

    def __len__(self) -> int:
        return len(self.coefs)

    def __getitem__(self, index: int) -> int:
        return self.coefs[index]

    def __setitem__(self, index: int, value: int) -> None:
        self.coefs[index] = value

    def __add__(self, other: 'Polynomial') -> 'Polynomial':
        max_len = max(len(self), len(other))
        result = Polynomial([0] * (max_len + 1))
        for i in range(max_len):
            x = self.coefs[i] if i < len(self) else 0
            y = other.coefs[i] if i < len(other) else 0
            result.coefs[i] = (x + y) % Polynomial.MOD
        result.shrink()
        return result

    def __neg__(self) -> 'Polynomial':
        return Polynomial([(Polynomial.MOD - x) % Polynomial.MOD for x in self.coefs])

    def __sub__(self, other) -> 'Polynomial':
        return self + (-other)

    def __mul__(self, other: 'Polynomial') -> 'Polynomial':
        result_size = len(self) + len(other) - 1
        result = Polynomial([0] * result_size)
        for i in range(len(self)):
            for j in range(len(other)):
                result.coefs[i + j] = (result.coefs[i + j] + self.coefs[i] * other.coefs[j]) % Polynomial.MOD
        result.shrink()
        return result

    def division(self, other: 'Polynomial', size: int) -> 'Polynomial':
        result = Polynomial([0] * size)
        if other.coefs[0] == 0:
            raise ZeroDivisionError
        result.coefs[0] = self.coefs[0] // other.coefs[0]
        for i in range(1, size):
            sum = 0
            for j in range(max(0, i - len(other)), i):
                if i - j < len(other):
                    sum += (other.coefs[i - j] * result.coefs[j]) % Polynomial.MOD
            sum %= Polynomial.MOD
            x = (self.coefs[i] - sum + Polynomial.MOD) % Polynomial.MOD if i < len(self) else (Polynomial.MOD - sum) % Polynomial.MOD
            result.coefs[i] = x // other.coefs[0]
        return result

    def print_polynomial(self, print_degree: bool = False) -> None:
        if print_degree:
            print(len(self.coefs) - 1)
        print(' '.join(map(str, self.coefs)))


def main():
    n, m = map(int, input().split())
    x = list(map(int, input().split()))
    y = list(map(int, input().split()))

    P1 = Polynomial(x)
    P2 = Polynomial(y)
    addition = P1 + P2
    mult = P1 * P2
    div = P1.division(P2, 1000)

    addition.print_polynomial(True)
    mult.print_polynomial(True)
    div.print_polynomial(False)


if __name__ == "__main__":
    main()
