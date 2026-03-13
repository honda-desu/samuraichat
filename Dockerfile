# ====== ビルド用ステージ ======
FROM maven:3-eclipse-temurin-17 AS build

# 作業ディレクトリ
WORKDIR /app

# 依存関係のキャッシュ用に pom.xml だけ先にコピー
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 残りのソースコードをコピー
COPY src ./src

# テストはスキップして JAR をビルド
RUN mvn clean package -DskipTests

# ====== 実行用ステージ ======
FROM eclipse-temurin:17-jre-alpine

# 非rootユーザーを作成（セキュリティ対策）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# ビルドステージから JAR をコピー
COPY --from=build /app/target/*.jar app.jar

# Render などの PaaS が PORT 環境変数を使うことを想定
ENV PORT=8080
EXPOSE 8080

# 非rootユーザーで実行
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]