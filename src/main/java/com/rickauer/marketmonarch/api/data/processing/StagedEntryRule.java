package com.rickauer.marketmonarch.api.data.processing;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.rules.AbstractRule;

public class StagedEntryRule extends AbstractRule {

	private final Rule _phase1Rule;
	private final Rule _phase2Rule;
	private boolean _waitingForMomentum;
	
	public StagedEntryRule(Rule phase1Rule, Rule phase2Rule) {
		_phase1Rule = phase1Rule;
		_phase2Rule = phase2Rule;
		_waitingForMomentum = false;
	}
	
	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		if (!_waitingForMomentum) {
			if (_phase1Rule.isSatisfied(index, tradingRecord)) {
				_waitingForMomentum = true;
			}
		} else {
			if (_phase2Rule.isSatisfied(index, tradingRecord)) {
				_waitingForMomentum = false;
				return true;
			}
		}
		return false;
	}

}
