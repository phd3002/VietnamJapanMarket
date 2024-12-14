# Sử dụng image Java base
FROM eclipse-temurin:17-jdk-alpine

# Cài đặt múi giờ bằng cách thêm tzdata
ENV TZ=Asia/Ho_Chi_Minh
RUN apk --no-cache add tzdata \
    && ln -sf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép tệp JAR từ thư mục local vào container
COPY target/shop-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng mà ứng dụng Spring Boot lắng nghe (thường là 8080)
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
