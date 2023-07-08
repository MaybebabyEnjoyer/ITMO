#include "LN.h"
#include "return_codes.h"

#include <fstream>
#include <iostream>
#include <stack>

#define push_check(x, f)                                                                                               \
	try                                                                                                                \
	{                                                                                                                  \
		stack.emplace(x);                                                                                              \
	} catch (std::bad_alloc & e)                                                                                       \
	{                                                                                                                  \
		std::cerr << "Out of memory";                                                                                  \
		return ERROR_OUT_OF_MEMORY;                                                                                    \
		f.close();                                                                                                     \
	}

int isNumber(const std::string &str)
{
	if (str[0] == '-' && str.size() == 1)
		return 0;

	auto is_valid_char = [](char i)
	{
		return (i >= '0' && i <= '9') || (i >= 'a' && i <= 'f') || (i >= 'A' && i <= 'F') || (i == '-');
	};

	return std::find_if_not(str.begin(), str.end(), is_valid_char) == str.end();
}

std::string to_uppercase(std::string str)
{
	std::transform(str.begin(), str.end(), str.begin(), ::toupper);
	return str;
}

int main(int argc, char *argv[])
{
	if (argc != 3)
	{
		std::cerr << "Expected 2 arguments, got " << argc - 1 << std::endl;
		std::cerr << "Usage: " << argv[0] << " <input file> <output file>" << std::endl;
		return ERROR_PARAMETER_INVALID;
	}

	std::ifstream in(argv[1]);
	if (!in.is_open())
	{
		std::cerr << "Cannot open file " << argv[1] << std::endl;
		return ERROR_CANNOT_OPEN_FILE;
	}

	std::stack< LN > stack;
	std::string str;
	while (in >> str)
	{
		if (isNumber(str))
		{
			LN ln(to_uppercase(str));
			push_check(ln, in);
		}
		else
		{
			LN ln1 = stack.top();
			stack.pop();
			if (str == "~")
			{
				push_check(~ln1, in)
			}
			else if (str == "_")
			{
				push_check(-ln1, in)
			}
			else
			{
				LN ln2 = stack.top();
				stack.pop();
				if (str == "+")
				{
					push_check(ln1 + ln2, in)
				}
				if (str == "-")
				{
					push_check(ln1 - ln2, in)
				}
				if (str == "*")
				{
					push_check(ln1 * ln2, in)
				}
				if (str == "/")
				{
					push_check(ln1 / ln2, in)
				}
				if (str == "%")
				{
					push_check(ln1 % ln2, in)
				}
				if (str == "<")
				{
					push_check(ln1 < ln2 ? 1 : 0, in)
				}
				if (str == ">")
				{
					push_check(ln1 > ln2 ? 1 : 0, in)
				}
				if (str == "<=")
				{
					push_check(ln1 <= ln2 ? 1 : 0, in)
				}
				if (str == ">=")
				{
					push_check(ln1 >= ln2 ? 1 : 0, in)
				}
				if (str == "==")
				{
					push_check(ln1 == ln2 ? 1 : 0, in)
				}
				if (str == "!=")
				{
					push_check(ln1 != ln2 ? 1 : 0, in)
				}
			}
		}
	}

	in.close();

	std::ofstream out(argv[2]);
	if (!out.is_open())
	{
		std::cerr << "Cannot open file " << argv[2] << std::endl;
		return ERROR_CANNOT_OPEN_FILE;
	}
	while (!stack.empty())
	{
		out << to_string(stack.top()) << std::endl;
		stack.pop();
	}

	out.close();
	return SUCCESS;
}