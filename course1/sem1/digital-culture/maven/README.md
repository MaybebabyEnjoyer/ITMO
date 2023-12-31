Чтобы сделать первую часть задания надо просто запуститься профайлером в идее (а также настроить application на jar)

Я верю что это осилить можно xd

# Build Configuration and CI/CD

В этом задании вам предоставлен несложный проект, написанный на Java. Вам нужно будет выполнить следующие задачи:

1. Написать файл конфигурации для вашей любимой системы сборки (*Maven* или *gradle*), который мог бы собирать этот проект
   и исполнять тесты. Не забывайте про зависимости этого проекта и его тестов. Обратите внимание на комментарии в файле `CloudTest.java` и то, при каких условиях он исполняется.

2. Добавить файл конфигурации в ваш репозиторий на **GitHub** и написать workflow для **GitHub Actions**, которая бы собирала ваш проект и запускала тесты, в том числе `CloudTest`.
   В этой задаче рекомендуется воспользоваться тем, что вы написали в предыдущем пункте.

Дополнительные требования:
- Во время выполнения всех заданий **запрещено** изменять файлы, которые уже присутствуют в репозитории, а также добавлять новые файлы в папку `src/`;
- Файл для workflow должен называться `test.yaml`;
- Job, исполняющий ваши тесты, должен называться `test-maven` или `test-gradle` соответственно;
- Если вы используете *gradle*, то задача, исполняющая тесты, должна называться `test`. Обратите внимание на существование интеграции `JUnit5` и *gradle*;
- Если вы используете *maven*, то для тестов должен использоваться специальный плагин.
