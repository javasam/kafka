# Тестовое задание Kafka
Создать два приложения: клиент и сервер. Должна быть возможность запустить несколько экземпляров клиента. Ответы при этом не должны теряться. Функциональность приложений следующая:

Клиент
Имеет интерфейс в виде командной строки. Умеет выполнять следующие операции:

Пополнение счета клиента (аргументы ID счета и сумма операции)

Списание со счета клиента (аргументы ID счета и сумма операции)

Перевод между счетами клиентов (аргументы ID счетов  и сумма операции)

Создание счета клиента (аргумент ID счета)

Закрытие счета клиента (аргумент ID счета) 

Клиент отправляет запросы на операции в целевую систему-сервер через Kafka

Сервер
Получает сообщения из очереди и выполняет операции. Ответы помещает в другую очередь, из которой их считывают клиенты.
