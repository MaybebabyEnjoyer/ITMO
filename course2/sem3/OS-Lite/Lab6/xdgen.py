import random

for i in range(1, 21):
    with open(f"file{i}.txt", "w") as f:
        for j in range(1, 2):
            f.write(str(random.randint(1, 1000)) + "\n")
