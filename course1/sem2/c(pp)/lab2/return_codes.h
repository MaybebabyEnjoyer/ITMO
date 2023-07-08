#pragma once

#ifndef SUCCESS
// The operation completed successfully
#	define SUCCESS 0
#endif

#ifndef ERROR_CANNOT_OPEN_FILE
// File can't be opened
#	define ERROR_CANNOT_OPEN_FILE 1
#endif

#ifndef ERROR_OUT_OF_MEMORY
// Not enough memory, memory allocation failed
#	define ERROR_OUT_OF_MEMORY 2
#endif

#ifndef ERROR_DATA_INVALID
// The data is invalid
#	define ERROR_DATA_INVALID 3
#endif

#ifndef ERROR_PARAMETER_INVALID
// The parameter or number of parameters (argv) is incorrect
#	define ERROR_PARAMETER_INVALID 4
#endif

#ifndef ERROR_UNSUPPORTED
// Unsupported functionality
#	define ERROR_UNSUPPORTED 5
#endif

#ifndef ERROR_UNKNOWN
// Other errors
#	define ERROR_UNKNOWN 10
#endif
