line = input().split()
stack = []
for i in range(len(line)):
    if line[i] == '+':
        res = stack[len(stack) - 2] + stack[len(stack) - 1]
        del stack[len(stack) - 2]
        del stack[len(stack) - 1]
        stack.append(res)
    elif line[i] == '-':
        res = stack[len(stack) - 2] - stack[len(stack) - 1]
        del stack[len(stack) - 2]
        del stack[len(stack) - 1]
        stack.append(res)
    elif line[i] == '*':
        res = stack[len(stack) - 2] * stack[len(stack) - 1]
        del stack[len(stack) - 2]
        del stack[len(stack) - 1]
        stack.append(res)
    else:
        stack.append(int(str(line[i])))
print(stack[0])
