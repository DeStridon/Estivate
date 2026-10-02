package com.estivate.test.reconciliation.resolvers.excluded;

import com.estivate.context.Context;
import com.estivate.reconciliation.EstivateReconciliation.AddColumnDelta;
import com.estivate.reconciliation.EstivateReconciliation.IAddColumnResolver;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationScope;

@ReconciliationScope
public class ExcludedPackageResolver implements IAddColumnResolver {

	@Override
	public void resolve(Context context, AddColumnDelta diff) {
		diff.closeSkipped("test resolver");
	}
}
