#pragma once
#include <algorithm>
#include <string>
#include <utility>
#include <vector>

class LN
{
  public:
	explicit LN(long long a = 0);

	LN(const LN &other);
	LN(LN &&other) noexcept;

	explicit LN(const char *str);
	explicit LN(const std::string_view &s);
	LN(uint32_t);
	LN(int a);

	LN &operator=(const LN &that);
	LN &operator=(LN &&that) noexcept;

	~LN() = default;

	explicit operator bool() const;
	explicit operator long long() const;

	LN operator~() const;
	LN operator-() const;

	friend LN operator+(LN, LN const &);
	friend LN operator-(LN, LN const &);
	friend LN operator*(LN const &, LN const &);
	friend LN operator/(LN const &, LN const &);
	friend LN operator%(LN const &, LN const &);

	LN &operator+=(LN const &);
	LN &operator-=(LN const &);
	LN &operator*=(LN const &);
	LN &operator/=(LN const &);
	LN &operator%=(LN const &);

	friend bool operator==(LN const &, LN const &);
	friend bool operator<(LN const &, LN const &);
	friend bool operator>(LN const &, LN const &);
	friend bool operator<=(LN const &, LN const &);
	friend bool operator>=(LN const &, LN const &);
	friend bool operator!=(LN const &, LN const &);

	friend std::string to_string(LN const &);

  private:
	std::vector< uint32_t > digits_;
	bool sign_;
	bool isNaN_;

	[[nodiscard]] int compareTo(LN const &) const;
	[[nodiscard]] int absCompare(LN const &) const;

	void convert(size_t);
	void format();
	LN abs() const;
	bool less(LN const &, size_t) const;
	void diff(LN const &, size_t);
	LN divide_LN_by_uint32(uint32_t) const;
	void bitwise_not();
	void negate();
	uint32_t get(size_t) const;
	void add(LN const &, bool);
};
LN operator""_ln(const char *s);