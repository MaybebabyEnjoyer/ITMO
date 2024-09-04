class Poly:
	MOD = 2 ** 63 - 1

	def __init__(self, *coeffs):
		if len(coeffs) == 1 and isinstance(coeffs[0], list):
			coeffs = coeffs[0]
		self.coeffs = [coef % self.MOD for coef in coeffs]

	def __getitem__(self, index):
		return self.coeffs[index] if index < len(self.coeffs) else 0

	def add(self, other):
		max_len = max(len(self.coeffs), len(other.coeffs))
		self.coeffs.extend([0] * (max_len - len(self.coeffs)))
		for i in range(len(other.coeffs)):
			self.coeffs[i] = (self.coeffs[i] + other.coeffs[i]) % self.MOD
			if self.coeffs[i] >= self.MOD:
				self.coeffs[i] -= self.MOD

	def negate(self):
		for i in range(len(self.coeffs)):
			self.coeffs[i] = (self.MOD - self.coeffs[i]) % self.MOD

	def subtract(self, other):
		self.negate()
		self.add(other)

	def multiply(self, other):
		result = [0] * (len(self.coeffs) + len(other.coeffs) - 1)
		for i in range(len(self.coeffs)):
			for j in range(len(other.coeffs)):
				result[i + j] = (result[i + j] + self.coeffs[i] * other.coeffs[j]) % self.MOD
				if result[i + j] >= self.MOD:
					result[i + j] -= self.MOD
		self.coeffs = result

	def copy(self):
		return Poly(self.coeffs.copy())

	def multiply_on_const_and_copy(self, const):
		return Poly([(x * const) % self.MOD for x in self.coeffs])

	def truncate(self, m):
		self.coeffs = self.coeffs[:m]

	@staticmethod
	def bin_pow(a, n):
		result = 1
		while n > 0:
			if n % 2 == 1:
				result = (result * a) % Poly.MOD
			a = (a * a) % Poly.MOD
			n //= 2
		return result

	def get_reverse_element(self, x):
		return self.bin_pow(x, self.MOD - 2)

	def __str__(self):
		return ' '.join(
			map(str, [((x + self.MOD) % self.MOD) - self.MOD if x > self.MOD // 2 else x for x in self.coeffs]))

	def deg(self):
		for i in reversed(range(len(self.coeffs))):
			if self.coeffs[i] != 0:
				return i + 1
		return 0

	def truncate_to_deg(self):
		self.truncate(self.deg())

	def print_with_deg(self):
		print(self.deg() - 1)
		print(self)

	def get_value(self, t):
		result = 0
		t_pow = 1
		for coef in self.coeffs:
			result = (result + coef * t_pow) % self.MOD
			t_pow = (t_pow * t) % self.MOD
		return result


if __name__ == "__main__":
	r = int(input())
	d = int(input())
	p = list(map(int, input().split()))

	dummy = Poly(1, -r)
	q_powered = Poly(1, -r)
	for i in range(d):
		q_powered.multiply(dummy)

	q = q_powered.copy()
	P = Poly(p)

	power = 1
	a_list = []
	for i in range(d + 1):
		value = P.get_value(i)
		value = (value * power) % Poly.MOD
		a_list.append(value)
		power = (power * r) % Poly.MOD

	Pol = Poly(a_list)
	Pol.truncate_to_deg()
	P = Poly(Pol.coeffs)
	P.multiply(q)
	P.truncate(d + 1)
	q.truncate_to_deg()
	P.print_with_deg()
	q.print_with_deg()
