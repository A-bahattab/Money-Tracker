package com.blogspot.e_kanivets.moneytracker.controller.data

import com.blogspot.e_kanivets.moneytracker.controller.base.BaseController
import com.blogspot.e_kanivets.moneytracker.entity.data.Transfer
import com.blogspot.e_kanivets.moneytracker.repo.base.IRepo

/**
 * Controller class to encapsulate transfer handling logic.
 * Created on 2/17/16.
 *
 * @author Evgenii Kanivets
 */
class TransferController(
    transferRepo: IRepo<Transfer>,
    private val accountController: AccountController
) : BaseController<Transfer>(transferRepo) {

    override fun create(transfer: Transfer?): Transfer? {
        val createdTransfer = repo.create(transfer)

        return if (createdTransfer == null) null else {
            accountController.transferDone(createdTransfer)
            createdTransfer
        }
    }

}
