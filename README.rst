------------
Demo Sample1
------------

Pengantar
---------

Ada beberapa hal yang diperagakan oleh program ini:

* Object Level Permissions, bagaimana menggunakan Spring Security's Permission
  Evaluator untuk implementasi hak akses ke data.

* Bagaimana mengkompilasi asset jadi js/css menggunakan Maven generate-resources.

* Bagaimana menjalankan tests dan static code analysis.

* Bagaimana menggunakan docker untuk pengerjaan program.


Tentang Website
---------------

Ada 3 Company yang tersusun berdasarkan hirarki (salah satunya adalah perusahaan
induk), dan 4 user role yang tersedia: admin, manager, supervisor, user. 

Supervisor bisa melihat user di Company mereka sendiri, Manager bisa melihat
Supervisor di Company mereka dan sub-Company di bawahnya, Admin bisa melihat dan
mengubah data dari semua role yang ada di Company mereka.


Cara Pakai
----------

A. Panduan untuk menyiapkan sistemnya:

1. buat file application.properties dan application-test.properties dengan menyalin
   dari file src/main/resources/application.properties.example
2. make start
3. make test

B. Website-nya sekarang bisa diakses di http://127.0.0.1:8080/dashboard


Daftar user yang bisa dipakai untuk login
-----------------------------------------

* admin@ptxyz.com
* admin@ptxyz1.com
* admin@ptxyz2.com
* manager1@ptxyz.com
* supervisor1@ptxyz1.com
* supervisor1@ptxyz2.com
* user1@ptxyz1.com
* user1@ptxyz2.com

Password-nya semua adalah 'pass'.
