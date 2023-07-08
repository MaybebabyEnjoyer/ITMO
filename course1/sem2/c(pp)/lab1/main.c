#include "return_codes.h"

#include <complex.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ROTATE_MATRIX(idx1, idx2)                                                                                      \
	{                                                                                                                  \
		tmp = cos * ELEM(idx1) + sin * ELEM(idx2);                                                                     \
		ELEM(idx2) = -sin * ELEM(idx1) + cos * ELEM(idx2);                                                             \
		ELEM(idx1) = tmp;                                                                                              \
	}

#define UPDATE_SHIFT(value)                                                                                            \
	{                                                                                                                  \
		for (int j = 0; j < DIMENSION; ++j)                                                                            \
		{                                                                                                              \
			matrix->elements[j * DIMENSION + j] += (value);                                                            \
		}                                                                                                              \
	}

#define DIMENSION matrix->n

#define ELEM(i) matrix->elements[i]

#define DATA matrix->elements

typedef struct
{
	int n;
	double *elements;
} Matrix;

typedef struct
{
	Matrix Q;
	Matrix R;
} Pair;

typedef struct
{
	complex double a;
	complex double b;
} Complex;

void readMatrix(Matrix *matrix, FILE *file)
{
	for (int i = 0; i < DIMENSION * DIMENSION; ++i)
	{
		fscanf(file, "%lf", &ELEM(i));
	}
}

void printAns(complex double *ans, int n, FILE *file)
{
	for (int i = 0; i < n; ++i)
	{
		if (fabs(creal(ans[i])) < 1e-8)
		{
			ans[i] -= creal(ans[i]);
		}
		if (fabs(cimag(ans[i])) < 1e-8)
		{
			ans[i] -= cimag(ans[i]) * I;
		}
		fprintf(file, "%g", creal(ans[i]));
		if (cimag(ans[i]) != 0)
		{
			if (cimag(ans[i]) > 0)
			{
				fprintf(file, " +%gi", cimag(ans[i]));
			}
			else
			{
				fprintf(file, " %gi", cimag(ans[i]));
			}
		}
		fprintf(file, "\n");
	}
}

inline static void updateMatrix(Matrix *matrix, int row1, int row2, double cos, double sin, int isTranspose)
{
	int limit = DIMENSION;
	for (int i = 0; i < limit; ++i)
	{
		int idx1 = isTranspose ? i * DIMENSION + row1 : row1 * DIMENSION + i;
		int idx2 = isTranspose ? i * DIMENSION + row2 : row2 * DIMENSION + i;
		double tmp = ELEM(idx1);
		ELEM(idx1) = ELEM(idx1) * cos + ELEM(idx2) * sin;
		ELEM(idx2) = ELEM(idx2) * cos - tmp * sin;
	}
}

int Init(Matrix *matrix, int n)
{
	DIMENSION = n;
	DATA = malloc(sizeof(double) * n * n);
	if (!DATA)
	{
		return 1;
	}
	return 0;
}

void hsb_rotate(Matrix *matrix1, Matrix *matrix2, int line)
{
	double cos;
	double sin;
	double x = matrix2->elements[line * matrix2->n + line];
	double y = matrix2->elements[(line + 1) * matrix2->n + line];
	double squared_sum = x * x + y * y;
	double sqrt_squared_sum = sqrt(squared_sum);

	if (sqrt_squared_sum != 0)
	{
		cos = x / sqrt_squared_sum;
		sin = y / sqrt_squared_sum;
	}
	else
	{
		cos = 1;
		sin = 0;
	}

	updateMatrix(matrix2, line, line + 1, cos, sin, 0);
	updateMatrix(matrix1, line, line + 1, cos, sin, 0);
	updateMatrix(matrix1, line, line + 1, cos, sin, 1);
}

Pair hsb_QR(Matrix *matrix)
{
	Matrix R;
	Matrix Q;

	if (Init(&R, DIMENSION) == 1 || Init(&Q, DIMENSION) == 1)
	{
		// Функция возвращает Пару, но честно не хочется тащить пару ниже и проверять где-нибудь потом на нулл
		// Это возможно была бы лучшая практика, но мне кажется что в этом месте exit будет смотреться достаточно
		// неплохо
		fprintf(stderr, "cannot allocate memory\n");
		exit(ERROR_OUT_OF_MEMORY);
	}

	memcpy(R.elements, DATA, DIMENSION * DIMENSION * sizeof(double));
	memcpy(Q.elements, DATA, DIMENSION * DIMENSION * sizeof(double));

	for (int i = 0; i < DIMENSION - 1; ++i)
	{
		hsb_rotate(&Q, &R, i);
	}

	return (Pair){ Q, R };
}

void hsb_decompose(Matrix *matrix)
{
	int i, j, k;
	double cos, sin, tmp, denom;

	for (k = 0; k < DIMENSION - 2; k++)
	{
		for (i = k + 2; i < DIMENSION; i++)
		{
			denom = hypot(ELEM((k + 1) * DIMENSION + k), ELEM(i * DIMENSION + k));

			if (fabs(denom) > 1e-9)
			{
				cos = ELEM((k + 1) * DIMENSION + k) / denom;
				sin = ELEM(i * DIMENSION + k) / denom;
			}
			else
			{
				cos = 1;
				sin = 0;
			}

			for (j = k; j < DIMENSION; j++)
			{
				ROTATE_MATRIX((k + 1) * DIMENSION + j, i * DIMENSION + j);
			}
			for (j = 0; j < DIMENSION; j++)
			{
				ROTATE_MATRIX(j * DIMENSION + (k + 1), j * DIMENSION + i);
			}
		}
	}
}

Complex complex_answer(double a, double b, double c, double d)
{
	double tr = a + d;
	double det = a * d - b * c;
	double discriminant = tr * tr - 4 * det;
	Complex res;

	if (discriminant >= 0)
	{
		double sqrt_discriminant = sqrt(discriminant);
		res.a = (tr + sqrt_discriminant) / 2;
		res.b = (tr - sqrt_discriminant) / 2;
	}
	else
	{
		double sqrt_abs_discriminant = sqrt(-discriminant);
		double real_part = tr / 2;
		double imag_part = sqrt_abs_discriminant / 2;
		res.a = real_part + imag_part * I;
		res.b = real_part - imag_part * I;
	}

	return res;
}

void find_eigenvalues(Matrix *matrix)
{
	hsb_decompose(matrix);
	int max_iterations = DIMENSION * DIMENSION * DIMENSION / 2;

	double avg = 0;
	for (int i = 0; i < DIMENSION * DIMENSION; ++i)
	{
		avg += fabs(ELEM(i));
	}
	avg /= (DIMENSION * DIMENSION);

	double eps = avg * 1e-5;

	double a = 0;
	for (int i = 0; i < DIMENSION - 1; ++i)
	{
		double value = fabs(ELEM((i + 1) * DIMENSION + i));
		if (value < eps)
		{
			a += value;
		}
	}

	for (int i = 0; i < max_iterations; ++i)
	{
		double shift = ELEM(DIMENSION * DIMENSION - 1);
		UPDATE_SHIFT(-shift)
		Pair qr = hsb_QR(matrix);

		free(DATA);

		*matrix = qr.Q;
		UPDATE_SHIFT(shift);
		qr.Q.elements = NULL;
		free(qr.R.elements);

		if (i > pow(10, log10(DIMENSION * DIMENSION) * 1.25) && a / (DIMENSION - 1) < 1e-12)
		{
			break;
		}
	}
}

void gen_ans(Matrix *matrix, complex double *res)
{
	int i = 0;
	while (i < DIMENSION)
	{
		if (fabs(matrix->elements[(i + 1) * DIMENSION + i]) > 1e-10 && i < DIMENSION - 1)
		{
			Complex tmp = complex_answer(
				ELEM(i * DIMENSION + i),
				ELEM(i * DIMENSION + i + 1),
				ELEM((i + 1) * DIMENSION + i),
				ELEM((i + 1) * DIMENSION + i + 1));
			res[i] = tmp.a;
			res[i + 1] = tmp.b;
			i += 2;
		}
		else
		{
			res[i] = ELEM(i * DIMENSION + i);
			i++;
		}
	}
	free(DATA);
}

int partition(complex double *elements, int low, int high)
{
	complex double pivot = elements[high];
	int i = low - 1;

	for (int j = low; j < high; ++j)
	{
		if (creal(elements[j]) > creal(pivot) || (creal(elements[j]) == creal(pivot) && cimag(elements[j]) >= cimag(pivot)))
		{
			++i;
			complex double temp = elements[i];
			elements[i] = elements[j];
			elements[j] = temp;
		}
	}

	complex double temp = elements[i + 1];
	elements[i + 1] = elements[high];
	elements[high] = temp;

	return i + 1;
}

void quick_sort(complex double *elements, int low, int high)
{
	if (low < high)
	{
		int pi = partition(elements, low, high);
		quick_sort(elements, low, pi - 1);
		quick_sort(elements, pi + 1, high);
	}
}

void sort_complex_numbers(complex double *elements, int size)
{
	quick_sort(elements, 0, size - 1);
}

int main(int argc, char *argv[])
{
	if (argc != 3)
	{
		fprintf(stderr, "Expected 2 arguments but found %i\n", argc - 1);
		return ERROR_PARAMETER_INVALID;
	}

	FILE *f;
	f = fopen(argv[1], "r");

	if (f == NULL)
	{
		fprintf(stderr, "File %s not found\n", argv[1]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	int n;
	fscanf(f, "%i", &n);
	Matrix matrix;

	if (Init(&matrix, n) == 1)
	{
		fprintf(stderr, "out of memory exception\n");
		fclose(f);
		return ERROR_OUT_OF_MEMORY;
	}

	readMatrix(&matrix, f);

	fclose(f);

	complex double *ans = malloc(n * sizeof(complex double));

	if (ans == NULL)
	{
		fprintf(stderr, "out of memory exception\n");
		free(matrix.elements);
		return ERROR_OUT_OF_MEMORY;
	}

	find_eigenvalues(&matrix);
	gen_ans(&matrix, ans);

	sort_complex_numbers(ans, n);

	f = fopen(argv[2], "w");
	if (f == NULL)
	{
		fprintf(stderr, "File %s not found\n", argv[2]);
		free(ans);
		free(matrix.elements);
		return ERROR_CANNOT_OPEN_FILE;
	}

	printAns(ans, n, f);
	fclose(f);
	free(ans);

	return SUCCESS;
}