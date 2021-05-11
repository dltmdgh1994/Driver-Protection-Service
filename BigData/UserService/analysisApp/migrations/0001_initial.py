# Generated by Django 3.2.2 on 2021-05-11 07:45

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('user_id', models.CharField(max_length=50, primary_key=True, serialize=False)),
                ('user_pwd', models.CharField(max_length=50)),
                ('name', models.CharField(max_length=50)),
                ('birth', models.CharField(max_length=50)),
                ('phone_number', models.CharField(max_length=50)),
                ('email', models.CharField(max_length=50)),
                ('gender', models.CharField(max_length=50)),
                ('car_kind', models.CharField(max_length=50)),
                ('serial_no1', models.CharField(max_length=50)),
                ('serial_no2', models.CharField(max_length=50)),
            ],
        ),
        migrations.CreateModel(
            name='Eye',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('time', models.DateField(auto_now_add=True)),
                ('is_sleep', models.IntegerField()),
                ('user_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='analysisApp.user')),
            ],
        ),
        migrations.CreateModel(
            name='Emotion',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('time', models.DateField(auto_now_add=True)),
                ('emotion', models.IntegerField()),
                ('user_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='analysisApp.user')),
            ],
        ),
        migrations.CreateModel(
            name='Co2',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('time', models.DateField(auto_now_add=True)),
                ('amount', models.IntegerField()),
                ('user_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='analysisApp.user')),
            ],
        ),
    ]