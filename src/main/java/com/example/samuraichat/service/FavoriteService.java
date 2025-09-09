package com.example.samuraichat.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Favorite;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.FavoriteRepository;

@Service
public class FavoriteService {
	public final FavoriteRepository favoriteRepository;

	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	// 指定したidを持つお気に入りを取得する
	public Optional<Favorite> findFavoriteById(Integer id) {
		return favoriteRepository.findById(id);
	}

	// 指定した店舗とユーザーが紐づいたお気に入りを取得する
	public Favorite findFavoriteByChatGroupAndUser(ChatGroup chatGroup, User user) {
		return favoriteRepository.findByChatGroupAndUser(chatGroup, user);
	}

	// 指定したユーザーのすべてのお気に入りを作成日時が新しい順に並べ替え、ページングされた状態で取得する
	public Page<Favorite> findFavoritesByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		return favoriteRepository.findByUserOrderByFavoritedAtDesc(user, pageable);
	}

	// お気に入りのレコード数を取得する
	public long countFavorites() {
		return favoriteRepository.count();
	}
	
	@Transactional
	public void createFavorite(ChatGroup chatGroup, User user) {
		Favorite favorite = new Favorite();
		
		favorite.setChatGroup(chatGroup);
		favorite.setUser(user);
		
		favoriteRepository.save(favorite);
	}
	
	@Transactional
	public void deleteFavorite(Favorite favorite) {
		favoriteRepository.delete(favorite);
	}
	
	   // 指定したユーザーが指定した店舗をすでにお気に入りに追加済みかどうかをチェックする
	public boolean isFavorite(ChatGroup chatGroup, User user) {
		return favoriteRepository.findByChatGroupAndUser(chatGroup, user) != null;
	}

}
