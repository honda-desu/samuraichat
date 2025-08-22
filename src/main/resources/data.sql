/* rolesテーブル */
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_GENERAL');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

/* usersテーブル（住所・電話番号関連の項目を削除） */
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled) VALUES
(1, '侍 太郎', 'サムライ タロウ', 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, true),
(2, '侍 花子', 'サムライ ハナコ', 'hanako.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 2, true),
(3, '侍 義勝', 'サムライ ヨシカツ', 'yoshikatsu.samurai@example.com', 'password', 1, false),
(4, '侍 幸美', 'サムライ サチミ', 'sachimi.samurai@example.com', 'password', 1, false),
(5, '侍 雅', 'サムライ ミヤビ', 'miyabi.samurai@example.com', 'password', 1, false),
(6, '侍 正保', 'サムライ マサヤス', 'masayasu.samurai@example.com', 'password', 1, false),
(7, '侍 真由美', 'サムライ マユミ', 'mayumi.samurai@example.com', 'password', 1, false),
(8, '侍 安民', 'サムライ ヤスタミ', 'yasutami.samurai@example.com', 'password', 1, false),
(9, '侍 章緒', 'サムライ アキオ', 'akio.samurai@example.com', 'password', 1, false),
(10, '侍 祐子', 'サムライ ユウコ', 'yuko.samurai@example.com', 'password', 1, false),
(11, '侍 秋美', 'サムライ アキミ', 'akimi.samurai@example.com', 'password', 1, false),
(12, '侍 信平', 'サムライ シンペイ', 'shinpei.samurai@example.com', 'password', 1, false);

/* chat_groupsテーブル */
INSERT IGNORE INTO chat_groups (id, name) VALUES
(1, '旅行好きの集い'),
(2, '広島観光情報'),
(3, '週末ハイキング部'),
(4, 'グルメ探検隊'),
(5, '写真共有グループ'),
(6, '英語学習サロン'),
(7, '猫好きチャット'),
(8, '朝活メンバー'),
(9, '読書クラブ'),
(10, '映画レビュー会'),
(11, 'プログラミング勉強会'),
(12, 'VBA職人の集い'),
(13, 'Excelマニア'),
(14, 'バックエンド開発者会'),
(15, 'SQL研究所'),
(16, 'データ分析ラボ'),
(17, '観光業界トレンド'),
(18, '地域別宿泊データ共有'),
(19, 'ホテル運営者ネットワーク'),
(20, '業務改善アイデア交換所'),
(21, '自動化ツール開発部'),
(22, 'API連携研究会'),
(23, '翻訳・ローカライズ相談室'),
(24, 'UI/UX改善チーム'),
(25, 'チャットボット開発者会'),
(26, '広島グルメマップ'),
(27, '廿日市市ローカル情報'),
(28, '温泉好きの会'),
(29, '旅館経営者フォーラム'),
(30, '観光統計データ共有');