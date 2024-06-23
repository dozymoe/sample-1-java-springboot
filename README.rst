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
