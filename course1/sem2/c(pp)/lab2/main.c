#include "return_codes.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#if defined(ZLIB)
#include <zlib.h>
#elif defined(LIBDEFLATE)
#include <libdeflate.h>
#elif defined(ISAL)
#include <include/igzip_lib.h>
#else
#error("wrong or not supported compression library")
#endif

#define MAX_PALETTE_LENGTH (256 * 3)
#define CHUNK_BUFF_LENGTH 128

unsigned char skip[CHUNK_BUFF_LENGTH];
unsigned char IHDR_name[4] = { 0x49, 0x48, 0x44, 0x52 };
unsigned char length_req[4] = { 0x00, 0x00, 0x00, 0x0D };
unsigned char IDAT_name[4] = { 0x49, 0x44, 0x41, 0x54 };
unsigned char PLTE_name[4] = { 0x50, 0x4C, 0x54, 0x45 };
unsigned char IEND_name[4] = { 0x49, 0x45, 0x4E, 0x44 };

void paeth(unsigned char *decompressed_data, int current_pixel, int left_pixel, int upper_pixel, int upper_left_pixel, int channel)
{
	int predicted = decompressed_data[left_pixel + channel] + decompressed_data[upper_pixel + channel] -
					decompressed_data[upper_left_pixel + channel];
	int diff_left = abs(predicted - decompressed_data[left_pixel + channel]);
	int diff_upper = abs(predicted - decompressed_data[upper_pixel + channel]);
	int diff_upper_left = abs(predicted - decompressed_data[upper_left_pixel + channel]);

	if (diff_left <= diff_upper && diff_left <= diff_upper_left)
	{
		decompressed_data[current_pixel + channel] += decompressed_data[left_pixel + channel];
	}
	else if (diff_upper <= diff_upper_left)
	{
		decompressed_data[current_pixel + channel] += decompressed_data[upper_pixel + channel];
	}
	else
	{
		decompressed_data[current_pixel + channel] += decompressed_data[upper_left_pixel + channel];
	}
}

void png_filters(unsigned char *decompressed_data, int width, int height, int bytes_per_pixel)
{
	int bytes_per_row = width * bytes_per_pixel + 1;

	for (int row = 0; row < height; row++)
	{
		unsigned char filter_type = decompressed_data[row * bytes_per_row];
		for (int col = 0; col < width; col++)
		{
			int current_pixel = row * bytes_per_row + col * bytes_per_pixel + 1;
			int left_pixel = (col == 0) ? 0 : current_pixel - bytes_per_pixel;
			int upper_pixel = (row == 0) ? 0 : current_pixel - bytes_per_row;
			int upper_left_pixel = (col == 0 || row == 0) ? 0 : current_pixel - bytes_per_row - bytes_per_pixel;

			for (int channel = 0; channel < bytes_per_pixel; channel++)
			{
				if (filter_type == 1 && col != 0)
				{
					decompressed_data[current_pixel + channel] += decompressed_data[left_pixel + channel];
				}
				else if (filter_type == 2 && row != 0)
				{
					decompressed_data[current_pixel + channel] += decompressed_data[upper_pixel + channel];
				}
				else if (filter_type == 3)
				{
					int avg_left = (col != 0) ? decompressed_data[left_pixel + channel] : 0;
					int avg_upper = (row != 0) ? decompressed_data[upper_pixel + channel] : 0;
					decompressed_data[current_pixel + channel] += (avg_left + avg_upper) / 2;
				}
				else if (filter_type == 4)
				{
					paeth(decompressed_data, current_pixel, left_pixel, upper_pixel, upper_left_pixel, channel);
				}
			}
		}
	}
}
int read_to_buff(FILE *input, unsigned char *buffer, unsigned int length)
{
	unsigned long read = fread(buffer, 1, length, input);
	if (read != length)
	{
		fprintf(stderr, "error while reading a file\n");
		return ERROR_DATA_INVALID;
	}

	return SUCCESS;
}

int check_name(unsigned char *name, unsigned char *check_name, int length)
{
	for (int i = 0; i < length; i++)
	{
		if (name[i] != check_name[i])
		{
			return ERROR_DATA_INVALID;
		}
	}

	return SUCCESS;
}

int from_ch_arr(unsigned char *arr, int start, int length)
{
	int result = arr[start] << (length - 1) * 8;
	for (int i = length - 2; i >= 0; i--)
	{
		result = result | (arr[start + length - i - 1] << i * 8);
	}
	return result;
}

int check_IHDR_data(unsigned char bit_depth, unsigned char color_type, unsigned char compression_method, unsigned char filter_method, unsigned char interlace_method)
{
	if (bit_depth != 0x08)
	{
		fprintf(stderr, "Unsupported bit depth %x ! bit depth should be 8\n", bit_depth);
		return ERROR_UNSUPPORTED;
	}
	if (color_type != 0x00 && color_type != 0x02 && color_type != 0x03)
	{
		fprintf(stderr, "Unsupported color type %x ! color type should be 0, 2 or 3\n", color_type);
		return ERROR_UNSUPPORTED;
	}

	if (compression_method != 0x00)
	{
		fprintf(stderr, "Invalid compression method %x ! compression method should be 0\n", compression_method);
		return ERROR_DATA_INVALID;
	}
	if (filter_method != 0x00)
	{
		fprintf(stderr, "Invalid filter method %x ! filter method should be 0\n", filter_method);
		return ERROR_DATA_INVALID;
	}
	if (interlace_method != 0x00 && interlace_method != 0x01)
	{
		fprintf(stderr, "Invalid interlace method %x ! interlace method should be 0 or 1\n", interlace_method);
		return ERROR_DATA_INVALID;
	}
	return SUCCESS;
}

int search_for_chunk(FILE *inputFile, unsigned char *name, unsigned char *buff, unsigned int *chunk_length)
{
	unsigned int len = *chunk_length + 4;
	if (check_name(name, IEND_name, 4) != SUCCESS)
	{
		if (read_to_buff(inputFile, buff, 4) != SUCCESS)
		{
			return ERROR_DATA_INVALID;
		}

		len = from_ch_arr(buff, 0, 4) + 4;

		if (read_to_buff(inputFile, buff, 4) != SUCCESS)
		{
			return ERROR_DATA_INVALID;
		}
	}

	while (check_name(buff, name, 4) != SUCCESS)
	{
		if (check_name(buff, PLTE_name, 4) == SUCCESS)
		{
			fprintf(stderr, "unexpected PLTE chunk\n");
			return ERROR_DATA_INVALID;
		}
		if (check_name(buff, IDAT_name, 4) == SUCCESS)
		{
			fprintf(stderr, "unexpected IDAT chunk\n");
			return ERROR_DATA_INVALID;
		}

		while (len > 0)
		{
			unsigned int bytes_to_read = len < 128 ? len : 128;

			if (read_to_buff(inputFile, skip, bytes_to_read) != SUCCESS)
			{
				return ERROR_DATA_INVALID;
			}

			len -= bytes_to_read;
		}
		if (fread(buff, 1, 4, inputFile) != 4)
		{
			return ERROR_DATA_INVALID;
		}

		len = from_ch_arr(buff, 0, 4) + 4;

		if (fread(buff, 1, 4, inputFile) != 4)
		{
			return ERROR_DATA_INVALID;
		}
	}
	*chunk_length = len - 4;
	return SUCCESS;
}

int main(int argc, char *argv[])
{
	FILE *inputFile, *outputFile;
	unsigned char png_name[8] = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

	unsigned char png_code[8];
	unsigned char buff[4];
	unsigned char readingIHDR[13];
	int width, length;
	unsigned char bit_depth, color_type, compression_method, filter_method, interlace_method;

	if (argc != 3)
	{
		fprintf(stderr, "Invalid format! expected two arguments - input file name and output file name.\n");
		fprintf(stderr, "Usage: %s <input_file_name> <output_file_name>\n", argv[0]);
		return ERROR_PARAMETER_INVALID;
	}

	if ((inputFile = fopen(argv[1], "rb")) == NULL)
	{
		fprintf(stderr, "Cannot open file %s\n", argv[1]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	if (read_to_buff(inputFile, png_code, 8) != SUCCESS)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	if (check_name(png_code, png_name, 8) != SUCCESS)
	{
		fprintf(stderr, "Is not a png (incorrect signature).\n");
		fclose(inputFile);
		return ERROR_PARAMETER_INVALID;
	}

	if (read_to_buff(inputFile, buff, 4) != SUCCESS)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	if (check_name(buff, length_req, 4) != SUCCESS)
	{
		fprintf(stderr, "Incorrect IHDR len\n");
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	if (read_to_buff(inputFile, buff, 4) != SUCCESS)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	if (check_name(buff, IHDR_name, 4) != SUCCESS)
	{
		fprintf(stderr, "expected IHDR \n");
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	if (read_to_buff(inputFile, readingIHDR, 13) != SUCCESS)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	width = from_ch_arr(readingIHDR, 0, 4);
	length = from_ch_arr(readingIHDR, 4, 4);

	if (width <= 0 || length <= 0)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	bit_depth = readingIHDR[8];
	color_type = readingIHDR[9];
	compression_method = readingIHDR[10];
	filter_method = readingIHDR[11];
	interlace_method = readingIHDR[12];

	int correct_data = check_IHDR_data(bit_depth, color_type, compression_method, filter_method, interlace_method);

	if (correct_data != SUCCESS)
	{
		fclose(inputFile);
		return correct_data;
	}

	if (read_to_buff(inputFile, buff, 4) != SUCCESS)
	{
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	unsigned char palette[MAX_PALETTE_LENGTH];
	unsigned int palette_length;
	if (color_type == 3)
	{
		if (search_for_chunk(inputFile, PLTE_name, buff, &palette_length) != SUCCESS)
		{
			fprintf(stderr, "couldn't find a pallet for color type 3 image.\n");
			fclose(inputFile);
			return ERROR_DATA_INVALID;
		}
		if (palette_length > 256 * 3 || palette_length % 3 != 0)
		{
			fprintf(stderr, "pallet size is not correct\n");
			return ERROR_DATA_INVALID;
		}
		if (read_to_buff(inputFile, palette, palette_length) != SUCCESS)
		{
			fclose(inputFile);
			return ERROR_DATA_INVALID;
		}
		if (read_to_buff(inputFile, buff, 4) != SUCCESS)
		{
			fclose(inputFile);
			return ERROR_DATA_INVALID;
		}
	}

	unsigned int chunk_length;
	if (search_for_chunk(inputFile, IDAT_name, buff, &chunk_length) != SUCCESS)
	{
		fprintf(stderr, "couldnt find a IDAT chunk.\n");
		fclose(inputFile);
		return ERROR_DATA_INVALID;
	}

	unsigned char *IDAT_data = NULL;
	unsigned int IDAT_data_length = 0;

	while (check_name(buff, IDAT_name, 4) == SUCCESS)
	{
		unsigned char *temp = realloc(IDAT_data, IDAT_data_length + chunk_length);
		if (temp == NULL)
		{
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_OUT_OF_MEMORY;
		}
		IDAT_data = temp;

		if (read_to_buff(inputFile, IDAT_data + IDAT_data_length, chunk_length) != SUCCESS)
		{
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_DATA_INVALID;
		}

		IDAT_data_length += chunk_length;
		if (read_to_buff(inputFile, buff, 4) != SUCCESS)	// crc
		{
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_DATA_INVALID;
		}

		if (read_to_buff(inputFile, buff, 4) != SUCCESS)
		{
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_DATA_INVALID;
		}

		chunk_length = from_ch_arr(buff, 0, 4);

		if (read_to_buff(inputFile, buff, 4) != SUCCESS)
		{
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_DATA_INVALID;
		}
	}

	if (check_name(buff, IEND_name, 4) != SUCCESS)
	{
		if (search_for_chunk(inputFile, IEND_name, buff, &chunk_length) != SUCCESS)
		{
			fprintf(stderr, "couldnt find a IEND chunk.\n");
			fclose(inputFile);
			free(IDAT_data);
			return ERROR_DATA_INVALID;
		}
	}

	if (chunk_length != 0)
	{
		fprintf(stderr, "IEND chunk length is not 0.\n");
		fclose(inputFile);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}

	if (read_to_buff(inputFile, buff, 4) != SUCCESS)	// crc
	{
		fclose(inputFile);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}

	if (fread(buff, 1, 1 + chunk_length, inputFile) == 1 + chunk_length)
	{
		fprintf(stderr, "there is more data after IEND chunk\n");
		fclose(inputFile);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}

	fclose(inputFile);

	int bytes_per_pixel = (color_type == 0x02 ? 3 : 1);
	int bytes_per_row = (width * bytes_per_pixel) + 1;
	unsigned long decompressed_data_size = length * bytes_per_row;

	unsigned char *decompressed_data = malloc(decompressed_data_size);

	if (decompressed_data == NULL)
	{
		fprintf(stderr, "out of memory\n");
		free(IDAT_data);
		return ERROR_OUT_OF_MEMORY;
	}

#if defined(ZLIB)
	uLongf destLen = decompressed_data_size;
	if (uncompress(decompressed_data, &destLen, IDAT_data, IDAT_data_length) != Z_OK)
	{
		fprintf(stderr, "failed to decompress data\n");
		free(decompressed_data);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}
#elif defined(LIBDEFLATE)
	struct libdeflate_decompressor *decompressor = libdeflate_alloc_decompressor();
	if (decompressor == NULL)
	{
		fprintf(stderr, "failed to allocate decompressor\n");
		free(decompressed_data);
		free(IDAT_data);
		return ERROR_OUT_OF_MEMORY;
	}
	size_t decompressed_data_actual_size;
	if (libdeflate_zlib_decompress(decompressor, IDAT_data, IDAT_data_length, decompressed_data, decompressed_data_size, &decompressed_data_actual_size) !=
		LIBDEFLATE_SUCCESS)
	{
		fprintf(stderr, "failed to decompress data\n");
		libdeflate_free_decompressor(decompressor);
		free(decompressed_data);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}
	libdeflate_free_decompressor(decompressor);

#elif defined(ISAL)
	struct inflate_state state;
	isal_inflate_init(&state);
	state.avail_in = IDAT_data_length;
	state.next_in = IDAT_data;
	state.avail_out = decompressed_data_size;
	state.next_out = decompressed_data;
	state.crc_flag = IGZIP_ZLIB;
	if (isal_inflate(&state) != ISAL_DECOMP_OK)
	{
		fprintf(stderr, "failed to decompress data\n");
		free(decompressed_data);
		free(IDAT_data);
		return ERROR_DATA_INVALID;
	}
#endif
	free(IDAT_data);

	png_filters(decompressed_data, width, length, bytes_per_pixel);
	if ((outputFile = fopen(argv[2], "wb")) == NULL)
	{
		fprintf(stderr, "cannot open file %s\n", argv[2]);
		free(decompressed_data);
		return ERROR_CANNOT_OPEN_FILE;
	}

	int p5 = color_type == 0x00;
	if (color_type == 0x03)
	{
		p5 = 1;
		for (int i = 0; i < palette_length; i += 3)
		{
			if (palette[i] != palette[i + 1] || palette[i] != palette[i + 2])
			{
				p5 = 0;
				break;
			}
		}
	}
	unsigned char *row;

	if (p5)
	{
		row = malloc(width);
		if (row == NULL)
		{
			fprintf(stderr, "out of memory\n");
			free(decompressed_data);
			fclose(outputFile);
			return ERROR_OUT_OF_MEMORY;
		}
		fprintf(outputFile, "P5\n%d %d\n%d\n", width, length, (1 << bit_depth) - 1);

		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < width; j++)
			{
				int index = (i * bytes_per_row) + j * bytes_per_pixel + 1;
				if (color_type == 0x00)
				{
					row[j] = decompressed_data[index];
				}
				else if (color_type == 0x03)
				{
					unsigned char palette_index = decompressed_data[index];
					row[j] = palette[palette_index * 3];
				}
			}
			fwrite(row, 1, width, outputFile);
		}
	}
	else
	{
		fprintf(outputFile, "P6\n%d %d\n%d\n", width, length, (1 << bit_depth) - 1);
		row = malloc(width * 3);
		if (row == NULL)
		{
			fprintf(stderr, "out of memory\n");
			free(decompressed_data);
			fclose(outputFile);
			return ERROR_OUT_OF_MEMORY;
		}
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < width; j++)
			{
				int index = (i * bytes_per_row) + j * bytes_per_pixel + 1;
				if (color_type == 0x02)
				{
					for (int k = 0; k < 3; ++k)
					{
						row[j * 3 + k] = decompressed_data[index + k];
					}
				}
				else if (color_type == 0x03)
				{
					unsigned char palette_index = decompressed_data[index];
					for (int k = 0; k < 3; ++k)
					{
						row[j * 3 + k] = palette[palette_index * 3 + k];
					}
				}
			}
			fwrite(row, 1, width * 3, outputFile);
		}
	}
	free(row);
	free(decompressed_data);
	fclose(outputFile);
	return SUCCESS;
}
