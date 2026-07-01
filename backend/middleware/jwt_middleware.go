package middleware

import (
	"net/http"
	"os"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

func JWTAuthMiddleware() gin.HandlerFunc {

	return func(c *gin.Context) {

		authHeader := c.GetHeader("Authorization")

		if authHeader == "" {

			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "Authorization header tidak ditemukan",
			})

			c.Abort()

			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")

		if tokenString == authHeader {

			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "Format token salah",
			})

			c.Abort()

			return
		}

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {

			return []byte(os.Getenv("JWT_SECRET")), nil

		})

		if err != nil || !token.Valid {

			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "Token tidak valid",
			})

			c.Abort()

			return
		}

		claims, ok := token.Claims.(jwt.MapClaims)

		if !ok {

			c.JSON(http.StatusUnauthorized, gin.H{
				"error": "Token tidak valid",
			})

			c.Abort()

			return
		}

		userID := uint(claims["user_id"].(float64))

		c.Set("userID", userID)

		c.Next()

	}
}