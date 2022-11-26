<h1>
CoffeeApp
</h1>
<em>
Приложение для торговых точек с ассортиментом не более 50 единиц продукции (Point Of Sale).<br>
Пишется в качестве практического проекта при изучении Java. <br>
В данном проекте не используются какие-либо технологии, frameworks и пр. <br>
Проект создан для сети из трёх кофеен и проходит проверки в реальных условиях на всех этапах реализации.
</em>
<br><br>
Данная система состоит из трёх частей:<br>
<br>
1. Окно аутентификации пользователя.<br>
2. Рабочий стол "Бариста".<br>
3. Рабочий стол "Администратора".<br>
<br>
<h3> Окно аутентификации пользователя: </h3>
При запуске программы происходит загрузка данных о пользователях<br>
из sql базы данных, после чего происходит сверка с данными, введёнными в поля формы.<br>
Кроме того, происходит загрузка данных о "торговых точках".<br>
После успешного прохождения аутентификации необходимо выбрать торговую точку, <br>
на которой будет открыта смена.<br>
<br>
<h3>Рабочий стол "Бариста"</h3>
Если, по результатам успешной аутентификации, пользователь не является администратором,<br>
Производится запуск данного Рабочего стола.<br>
Рабочий стол "Бариста" состоит из трёх рабочих блоков:<br>
1. Блок кнопок продуктов.<br>
2. Блок цифровых кнопок. (выбор количества продукта).<br>
3. Блок управление продажей. Блок, который включает в себя несколько панелей:<br>
<br>
<li>Панель кнопок со скидками. Данная панель отображает кнопки со скидками,<br>
которые допускает вводить Администратор системы<br></li>
<li>Панель формирования чека. Данная панель включает в себя кнопки выбора<br>
варианта формирования чека и оплаты.</li>
<li>Прочие кнопки управления продажами и информация о текущем чеке и позиции в чеке.</li>
