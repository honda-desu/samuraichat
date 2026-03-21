package com.example.samuraichat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraichat.entity.Block;
import com.example.samuraichat.entity.Report;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.BlockRepository;
import com.example.samuraichat.repository.ReportRepository;
import com.example.samuraichat.repository.UserRepository;

@Service
public class BlockAndReportService {
	
	private final UserRepository userRepository;
	private final BlockRepository blockRepository;
	private final ReportRepository reportRepository;
	
	public BlockAndReportService(UserRepository userRepository, BlockRepository blockRepository, ReportRepository reportRepository) {
		this.userRepository = userRepository;
		this.blockRepository = blockRepository;
		this.reportRepository = reportRepository;
	}
	
	@Transactional
	public void blockUser(Long blockerId, Long targetId, String reason) {

		User blocker = userRepository.findById(blockerId)
		        .orElseThrow(() -> new IllegalArgumentException("Blocker user not found"));

		User target = userRepository.findById(targetId)
		        .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
	    
	    if (blockerId.equals(targetId)) {
	        throw new IllegalArgumentException("自分自身をブロックすることはできません");
	    }

	    // すでにブロック済みなら何もしない
	    if (blockRepository.existsByBlockerIdAndBlockedUserId(blockerId, targetId)) {
	        return;
	    }

	    // ブロック登録
	    Block block = new Block();
	    block.setBlocker(blocker);
	    block.setBlockedUser(target);
	    blockRepository.save(block);

	    // 自動通報
	    Report report = new Report();
	    report.setReporter(blocker);
	    report.setTargetUser(target);
	    report.setReason(reason);
	    reportRepository.save(report);
	}
	
	// ★ 追加：ブロック判定
	public boolean isBlocked(Long blockerId, Long targetId) {
	    return blockRepository.existsByBlockerIdAndBlockedUserId(blockerId, targetId);
	}
	
	// ★ 追加：ブロック解除（後で使う）
    @Transactional
    public void unblock(Long blockerId, Long targetId) {
        blockRepository.deleteByBlockerIdAndBlockedUserId(blockerId, targetId);
    }


}
