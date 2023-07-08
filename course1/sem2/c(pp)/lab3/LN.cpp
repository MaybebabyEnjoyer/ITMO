#include "LN.h"

#include <cassert>
#include <ranges>
#include <stdexcept>

typedef unsigned __int128 uint128_t;

// Constructors //
LN::LN(long long x) : LN(static_cast< uint32_t >(std::abs(x)))
{
	if (x < 0)
	{
		sign_ = true;
	}
}

// assuming that NaN can't be created by user
LN::LN(const char *str)
{
	isNaN_ = false;
	sign_ = false;
	digits_.clear();
	for (const char *i = str; *i; ++i)
	{
		uint32_t x;
		if (*i >= '0' && *i <= '9')
		{
			x = *i - '0';
		}
		else if (*i >= 'A' && *i <= 'F')
		{
			x = *i - 'A' + 10;
		}
		else if (*i >= 'a' && *i <= 'f')
		{
			x = *i - 'a' + 10;
		}
		else
		{
			continue;
		}
		*this *= 16;
		*this += x;
	}
	if (str[0] == '-')
	{
		negate();
	}
}

LN::LN(uint32_t x) : sign_(false)
{
	isNaN_ = false;
	if (x != 0)
	{
		digits_.push_back(x);
	}
}

LN::LN(std::string_view const &s)
{
	isNaN_ = false;
	sign_ = false;
	digits_.clear();
	for (char i : s)
	{
		uint32_t x;
		if (i >= '0' && i <= '9')
		{
			x = i - '0';
		}
		else if (i >= 'A' && i <= 'F')
		{
			x = i - 'A' + 10;
		}
		else if (i >= 'a' && i <= 'f')
		{
			x = i - 'a' + 10;
		}
		else
		{
			continue;
		}
		*this *= 16;
		*this += x;
	}
	if (s.front() == '-')
	{
		negate();
	}
}

LN::LN(int x) : LN(static_cast< uint32_t >(std::abs(x)))
{
	if (x < 0)
	{
		sign_ = true;
	}
}

LN::LN(const LN &other)
{
	this->digits_ = other.digits_;
	this->sign_ = other.sign_;
	this->isNaN_ = other.isNaN_;
}

LN::LN(LN &&that) noexcept : sign_(that.sign_), isNaN_(that.isNaN_), digits_(std::move(that.digits_)) {}
///////////////////////////////////////////////////////////////////////////////

// Utility functions //
LN LN::abs() const
{
	return sign_ ? -(*this) : *this;
}

namespace
{
	uint32_t negate_digit(bool is_negated, uint32_t x, bool &c)
	{
		if (is_negated)
		{
			x ^= UINT32_MAX;
			x += c;
			if (x != 0)
			{
				c = false;
			}
		}
		return x;
	}

	uint32_t get_useless_digit(bool sign_)
	{
		return sign_ ? UINT32_MAX : 0;
	}

	void add_with_carry(uint32_t &a, uint32_t b, bool &c)
	{
		a += b + c;
		c = a < b + c || (b == UINT32_MAX && c);
	}

	uint32_t cast_64_32(uint64_t x)
	{
		return static_cast< uint32_t >(x & UINT32_MAX);
	}

	uint128_t right_shift_128(uint32_t x, int shift)
	{
		return static_cast< uint128_t >(x) << shift;
	}

	uint128_t or_shift_128(std::vector< uint32_t > const &digits_, size_t k)
	{
		uint128_t res = 0;
		for (size_t i = 1; i <= k; i++)
		{
			res |= right_shift_128(digits_[digits_.size() - i], 32 * (k - i));
		}
		return res;
	}
}	 // namespace

uint32_t LN::get(size_t i) const
{
	if (i < digits_.size())
	{
		return digits_[i];
	}
	else
	{
		return get_useless_digit(sign_);
	}
}

void LN::add(LN const &b, bool is_negated)
{
	if (!b.sign_ && b.digits_.empty())
	{
		return;
	}
	if (b.sign_ && b.digits_.empty() && is_negated)
	{
		add(LN(1LL), false);
		return;
	}
	convert(std::max(digits_.size(), b.digits_.size()));
	bool c = false;
	bool negate_c = is_negated;
	bool b_sign = b.sign_ ^ is_negated;
	for (size_t i = 0; i < digits_.size(); i++)
	{
		uint32_t x = negate_digit(is_negated, b.get(i), negate_c);
		add_with_carry(digits_[i], x, c);
	}
	if (negate_c)
	{
		digits_.push_back(get_useless_digit(sign_));
		add_with_carry(digits_.back(), negate_digit(is_negated, get_useless_digit(b.sign_), negate_c), c);
	}

	if (sign_ && b_sign)
	{
		if (!c)
		{
			digits_.push_back(UINT32_MAX - 1);
		}
	}
	else if (c)
	{
		if (sign_ || b_sign)
		{
			sign_ = false;
		}
		else
		{
			digits_.push_back(1);
		}
	}
	else
	{
		sign_ |= b_sign;
	}
	format();
}

void LN::convert(size_t sz)
{
	while (digits_.size() < sz)
	{
		digits_.push_back(sign_ ? UINT32_MAX : 0);
	}
}

void LN::format()
{
	while (!digits_.empty() && digits_.back() == (sign_ ? UINT32_MAX : 0))
	{
		digits_.pop_back();
	}
}

bool LN::less(LN const &b, size_t ind) const
{
	assert(!sign_ && !b.sign_);
	for (size_t i = 1; i <= digits_.size(); i++)
	{
		if (digits_[digits_.size() - i] != (ind - i < b.digits_.size() ? b.digits_[ind - i] : 0))
		{
			return digits_[digits_.size() - i] > (ind - i < b.digits_.size() ? b.digits_[ind - i] : 0);
		}
	}
	return true;
}

void LN::diff(LN const &b, size_t ind)
{
	assert(!sign_ && !b.sign_);
	size_t start = digits_.size() - ind;
	bool c = false;
	for (size_t i = 0; i < ind; ++i)
	{
		uint32_t x = digits_[start + i];
		uint32_t y = (i < b.digits_.size() ? b.digits_[i] : 0);
		uint64_t res = (static_cast< uint64_t >(x) - y) - c;
		c = (static_cast< uint64_t >(y) + c > x);
		digits_[start + i] = cast_64_32(res);
	}
}

LN LN::divide_LN_by_uint32(uint32_t b) const
{
	LN res;
	uint64_t c = 0;
	uint64_t x;
	for (size_t i = 0; i < digits_.size(); i++)
	{
		x = (c << 32) | digits_[digits_.size() - 1 - i];
		res.digits_.push_back(cast_64_32(x / b));
		c = x % b;
	}
	reverse(res.digits_.begin(), res.digits_.end());
	res.format();
	return res;
}

std::string to_string(LN const &x)
{
	if (x == 0)
	{
		return "0";
	}
	if (x.isNaN_)
	{
		return "NaN";
	}
	LN x_abs = x.abs();
	std::string res;
	while (x_abs > 0)
	{
		LN r = x_abs % 16;
		uint32_t c = (r == 0 ? 0 : r.digits_[0]);
		if (c < 10)
		{
			res.push_back('0' + c);
		}
		else
		{
			res.push_back('A' + c - 10);
		}
		x_abs /= 16;
	}
	if (x.sign_)
	{
		res.push_back('-');
	}
	std::reverse(res.begin(), res.end());
	return res;
}

void LN::bitwise_not()
{
	for (unsigned int &digit : digits_)
	{
		digit ^= UINT32_MAX;
	}
	sign_ ^= true;
}

void LN::negate()
{
	if (digits_.empty())
	{
		if (sign_)
		{
			sign_ = false;
			digits_.push_back(1);
		}
	}
	else
	{
		bitwise_not();
		*this += 1;
		format();
	}
}

///////////////////////////////////////////////////////////////////////////////

// Operators //

// Assignment
LN &LN::operator=(const LN &that)
{
	if (this != &that)
	{
		sign_ = that.sign_;
		isNaN_ = that.isNaN_;
		digits_ = that.digits_;
	}
	return *this;
}

LN &LN::operator=(LN &&that) noexcept
{
	if (this != &that)
	{
		sign_ = that.sign_;
		isNaN_ = that.isNaN_;
		digits_ = std::move(that.digits_);
	}
	return *this;
}

// Unary

LN LN::operator~() const
{
	if (this->isNaN_ || this->sign_)
	{
		LN res;
		res.isNaN_ = true;
		return res;
	}

	LN low(0LL), high(*this), mid;

	while (high - low > 1)
	{
		mid = (low + high) / 2;
		if (mid * mid < *this)
		{
			low = mid;
		}
		else
		{
			high = mid;
		}
	}

	if ((low + 1) * (low + 1) <= *this)
	{
		low = low + 1;
	}

	return low;
}

LN LN::operator-() const
{
	if (this->isNaN_)
	{
		return *this;
	}
	LN res = *this;
	res.negate();
	return res;
}

// Binary
LN operator+(LN a, LN const &b)
{
	if (a.isNaN_ || b.isNaN_)
	{
		a.isNaN_ = true;
		return a;
	}
	return a += b;
}

LN operator-(LN a, LN const &b)
{
	if (a.isNaN_ || b.isNaN_)
	{
		a.isNaN_ = true;
		return a;
	}
	return a -= b;
}

LN operator*(LN const &a, LN const &b)
{
	if (a.isNaN_ || b.isNaN_)
	{
		LN res;
		res.isNaN_ = true;
		return res;
	}
	LN res;
	res.convert(a.digits_.size() + b.digits_.size());
	bool ca = a.sign_;
	for (size_t i = 0; i < a.digits_.size() + ca; i++)
	{
		uint32_t xa = (i < a.digits_.size() ? negate_digit(a.sign_, a.digits_[i], ca) : ca);
		uint32_t c = 0;
		bool cb = b.sign_;
		for (size_t j = 0; j < b.digits_.size() + cb; j++)
		{
			uint64_t mul =
				static_cast< uint64_t >(xa) * (j < b.digits_.size() ? negate_digit(b.sign_, b.digits_[j], cb) : cb) +
				static_cast< uint64_t >(res.digits_[i + j]) + c;
			res.digits_[i + j] = cast_64_32(mul);
			c = mul >> 32;
		}
		res.digits_[i + b.digits_.size()] = c;
	}
	res.format();
	if (a.sign_ ^ b.sign_)
	{
		res.negate();
	}
	return res;
}

LN operator/(LN const &a, LN const &b)
{
	if (a.isNaN_ || b.isNaN_ || b == 0)
	{
		LN res;
		res.isNaN_ = true;
		return res;
	}
	LN a_abs = a.abs();
	LN b_abs = b.abs();
	LN res;
	LN dq;
	if (a_abs < b_abs)
	{
		return 0;
	}
	if (b_abs.digits_.size() == 1)
	{
		res = a_abs.divide_LN_by_uint32(b_abs.digits_[0]);
		if (a.sign_ ^ b.sign_)
		{
			res.negate();
		}
		return res;
	}
	a_abs.digits_.push_back(0);
	size_t m = b_abs.digits_.size() + 1;
	size_t n = a_abs.digits_.size();
	res.digits_.resize(n - m + 1);
	uint32_t qt;
	for (size_t i = m, j = res.digits_.size() - 1; i <= n; i++, j--)
	{
		uint128_t x = or_shift_128(a_abs.digits_, 3);
		uint128_t y = or_shift_128(b_abs.digits_, 2);
		qt = static_cast< uint32_t >(std::min(static_cast< uint128_t >(UINT32_MAX), x / y));
		dq = b_abs * qt;
		if (!a_abs.less(dq.abs(), m))
		{
			qt--;
			dq -= b_abs;
		}
		res.digits_[j] = qt;
		a_abs.diff(dq.abs(), m);
		if (a_abs.digits_.back() == 0)
		{
			a_abs.digits_.pop_back();
		}
	}
	res.format();
	if (a.sign_ ^ b.sign_)
	{
		res.negate();
	}
	return res;
}

LN operator%(LN const &a, LN const &b)
{
	return a - (a / b) * b;
}

LN &LN::operator+=(LN const &x)
{
	add(x, false);
	return *this;
}

LN &LN::operator-=(LN const &x)
{
	add(x, true);
	return *this;
}

LN &LN::operator*=(LN const &x)
{
	return *this = *this * x;
}

LN &LN::operator/=(LN const &x)
{
	return *this = *this / x;
}

LN &LN::operator%=(LN const &x)
{
	return *this = *this % x;
}

// Comparison

int LN::absCompare(const LN &that) const
{
	if (digits_.size() > that.digits_.size())
	{
		return 1;
	}
	else if (digits_.size() < that.digits_.size())
	{
		return -1;
	}
	else
	{
		for (auto i = digits_.rbegin(), j = that.digits_.rbegin(); i != digits_.rend(); ++i, ++j)
		{
			if (*i == *j)
			{
				continue;
			}
			return *i > *j ? 1 : -1;
		}
		return 0;
	}
}

int LN::compareTo(const LN &that) const
{
	if (sign_ != that.sign_)
	{
		return sign_ < that.sign_ ? 1 : -1;
	}
	else if (isNaN_ || that.isNaN_)
	{
		return 2;
	}
	else
	{
		int absComp = absCompare(that);
		if (absComp != 0)
		{
			return sign_ == 1 ? -absComp : absComp;
		}
		else
		{
			return 0;
		}
	}
}

bool operator==(const LN &lhs, const LN &rhs)
{
	return !lhs.isNaN_ && !rhs.isNaN_ && lhs.compareTo(rhs) == 0;
}

bool operator!=(const LN &lhs, const LN &rhs)
{
	return lhs.isNaN_ || rhs.isNaN_ || lhs.compareTo(rhs) != 0;
}

bool operator<(const LN &lhs, const LN &rhs)
{
	return !lhs.isNaN_ && !rhs.isNaN_ && lhs.compareTo(rhs) < 0;
}

bool operator>(const LN &lhs, const LN &rhs)
{
	return !lhs.isNaN_ && !rhs.isNaN_ && lhs.compareTo(rhs) > 0;
}

bool operator<=(const LN &lhs, const LN &rhs)
{
	return !lhs.isNaN_ && !rhs.isNaN_ && lhs.compareTo(rhs) <= 0;
}

bool operator>=(const LN &lhs, const LN &rhs)
{
	return !lhs.isNaN_ && !rhs.isNaN_ && lhs.compareTo(rhs) >= 0;
}
///////////////////////////////////////////////////////////////////////////////

// Overloaded cast operators/literal operator //
LN::operator bool() const
{
	return compareTo(LN(0LL)) != 0;
}

LN::operator long long() const
{
	if (digits_.size() > 2 || (digits_.size() == 2 && digits_[1] >= (1ull << 31)))
	{
		throw std::overflow_error("Number is too large to be casted to long long");
	}

	LN a = *this;
	if (sign_)
	{
		a.negate();
	}
	long long res = 0;
	for (unsigned int digit : std::ranges::reverse_view(a.digits_))
	{
		res = (res << 32) + digit;
	}
	sign_ ? res = -res : res;
	return res;
}

LN operator""_ln(const char *str)
{
	return LN(str);
}
