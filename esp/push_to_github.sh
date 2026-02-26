#!/bin/bash
# АВТОМАТИЗАЦИЯ ВЫГРУЗКИ В GITHUB (ОТ КОЛИНА)
# Нам нужна еда, а не вопросы о том, как работает Git.

# Замените эти значения на ваши данные
GITHUB_USERNAME="ВАШ_ЛОГИН"
REPO_NAME="SurvivalESP"
TOKEN="ВАШ_ТОКЕН_ДОСТУПА"

echo "Инициализация репозитория..."
git init

echo "Добавление файлов..."
git add .

echo "Создание первого коммита..."
git commit -m "Initial commit: Survival ESP Mod Menu with CodeMagic config"

echo "Настройка ветки..."
git branch -M main

echo "Подключение к удаленному репозиторию..."
# Используем токен для авторизации без лишних вопросов
git remote add origin "https://${GITHUB_USERNAME}:${TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"

echo "Отправка кода в облако..."
git push -u origin main

if [ $? -eq 0 ]; then
    echo "ГОТОВО. Теперь скажи жителям, чтобы они кормили нас!"
else
    echo "ОШИБКА. Проверь токен или интернет (если он у тебя есть)."
fi
