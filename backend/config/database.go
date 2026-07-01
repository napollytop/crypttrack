package config


import(
	"fmt"
	"os"
	"log"

	"backend/models"
	"github.com/joho/godotenv"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

var DB *gorm.DB

func ConnectDatabase(){
	err := godotenv.Load()

	if err != nil{
		log.Fatal("gagal membaca .env")
	}

	host := os.Getenv("DB_HOST")
	port := os.Getenv("DB_PORT")
	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")
	dbname := os.Getenv("DB_NAME")

	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?charset=utf8mb4&parseTime=True&loc=Local",
		user, password, host, port, dbname)

	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})

	err = DB.AutoMigrate(
	&models.User{},
	&models.Watchlist{},
)

if err != nil {
	log.Fatal("Migration gagal")
}

	if err != nil{
		log.Fatal("gagal terhubung ke database", err)
	}

	log.Println("terkoneksi")
}