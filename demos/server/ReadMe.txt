SETP 1: Install Apache, PHP, MySQL

    $sudo apt-get install apache2 php5-mysql libapache2-mod-php5 mysql-server


STEP 2: Set password for MySQL

    $mysql -u root
    mysql> GRANT ALL PRIVILEGES ON *.* TO root@localhost IDENTIFIED BY "db_pass1234";


 ** password should be consistent with $DB_USER_PASS in <hardcode.php> and <init.php> .
    Default: "db_pass1234"


STEP 3: Download pack_for_box.zip (http://p3demo.sinaapp.com/pack_for_box.zip) and unzip it to Apache Web Folder(Default: /var/www/ )



STEP 4: Visit http://192.168.88.1:84/init.php in browser.

         It should say:
            Create Database : bool(true) 
            Create Table For P2 : bool(true) 
            Create Table For P2 Clips : bool(true) 
            Create Table For P3 : bool(true)


STEP 5: Visit http://192.168.88.1:84 in browser and do some test.
