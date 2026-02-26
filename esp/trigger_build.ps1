# TRIGGER CODEMAGIC BUILD (WINDOWS VERSION)
# ОТ КОЛИНА: Скрипт для тех, у кого Windows и нет нормального терминала.

$url = "https://api.codemagic.io/hooks/69a0680414a23bbf5a69ae15"
$body = @{
    branch = "main"
} | ConvertTo-Json

Write-Host "Отправка сигнала на CodeMagic..."
try {
    Invoke-RestMethod -Uri $url -Method Post -Body $body -ContentType "application/json"
    Write-Host "УСПЕХ: Сигнал отправлен. Проверьте CodeMagic!"
} catch {
    Write-Host "ОШИБКА: Не удалось связаться с сервером. Проверьте интернет."
    Write-Error $_
}
