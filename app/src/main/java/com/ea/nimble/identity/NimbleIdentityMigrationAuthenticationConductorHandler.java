package com.ea.nimble.identity;

import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.Iterator;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityMigrationAuthenticationConductorHandler.class */
class NimbleIdentityMigrationAuthenticationConductorHandler implements NimbleIdentityAuthenticationConductorHandler {
    private INimbleIdentityMigrationAuthenticationConductor m_conductor;

    public NimbleIdentityMigrationAuthenticationConductorHandler(INimbleIdentityAuthenticationConductor iNimbleIdentityAuthenticationConductor) {
        this.m_conductor = (INimbleIdentityMigrationAuthenticationConductor) iNimbleIdentityAuthenticationConductor;
        List<INimbleIdentityAuthenticator> loggedInAuthenticators = NimbleIdentity.getComponent().getLoggedInAuthenticators();
        if (loggedInAuthenticators.size() > 0) {
            if (loggedInAuthenticators.size() != 1) {
                Iterator<INimbleIdentityAuthenticator> it = loggedInAuthenticators.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    INimbleIdentityAuthenticator next = it.next();
                    if (!next.getAuthenticatorId().equals(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS)) {
                        NimbleIdentityImpl.getComponent().setMainAuthenticator(next);
                        break;
                    }
                }
            } else {
                NimbleIdentityImpl.getComponent().setMainAuthenticator(loggedInAuthenticators.get(0));
            }
        }
        handlePendingMigration();
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogin(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, boolean z) {
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        INimbleIdentityAuthenticator mainAuthenticator = component.getMainAuthenticator();
        if (mainAuthenticator == null) {
            component.setMainAuthenticator(iNimbleIdentityAuthenticator);
        } else if (iNimbleIdentityAuthenticator == mainAuthenticator) {
            Log.Helper.LOGW(this, "Error. Attempted to handle login on a authenticator: %s which is already mainAuthenticator", iNimbleIdentityAuthenticator.getAuthenticatorId());
        } else if (!handlePendingMigration()) {
            this.m_conductor.handleLogin(new NimbleIdentityMigrationLoginResolver(iNimbleIdentityAuthenticator));
        }
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogout(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        if (iNimbleIdentityAuthenticator == null) {
            Log.Helper.LOGF(this, "Given a null authenticator as part of logout proess.", new Object[0]);
        } else if (!iNimbleIdentityAuthenticator.getAuthenticatorId().equals(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS)) {
            NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
            AuthenticatorBase authenticatorBaseById = component.getAuthenticatorBaseById(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS);
            if (authenticatorBaseById == null) {
                Log.Helper.LOGF(this, "Unable to set Anonymous Authenticator as main authenticator as part of logout process.", new Object[0]);
                return;
            }
            component.setMainAuthenticator(authenticatorBaseById);
            if (authenticatorBaseById.getState() != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                authenticatorBaseById.autoLogin();
            }
        }
    }

    public boolean handlePendingMigration() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.identity", Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent == null) {
            Log.Helper.LOGW(this, "Attempted to check pending migration status but persistence was not available. Failing.", new Object[0]);
            return false;
        }
        NimbleIdentityMigrationObject nimbleIdentityMigrationObject = (NimbleIdentityMigrationObject) persistenceForNimbleComponent.getValue(INimbleIdentity.MIGRATION_PERSISTENCE_ID);
        if (nimbleIdentityMigrationObject == null) {
            Log.Helper.LOGI(this, "No pending migration object found in persistence!", new Object[0]);
            return false;
        }
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        AuthenticatorBase authenticatorBaseById = component.getAuthenticatorBaseById(nimbleIdentityMigrationObject.m_currentAuthenticatorId);
        if (authenticatorBaseById.getState() != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS || !authenticatorBaseById.getPidInfo().getPid().equals(nimbleIdentityMigrationObject.m_currentAuthenticatorPid)) {
            return false;
        }
        AuthenticatorBase authenticatorBaseById2 = component.getAuthenticatorBaseById(nimbleIdentityMigrationObject.m_newAuthenticatorId);
        if (authenticatorBaseById2.getState() != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS || !authenticatorBaseById2.getPidInfo().getPid().equals(nimbleIdentityMigrationObject.m_newAuthenticatorPid)) {
            return false;
        }
        NimbleIdentityPendingMigrationResolver nimbleIdentityPendingMigrationResolver = new NimbleIdentityPendingMigrationResolver(nimbleIdentityMigrationObject.m_migrationGUID, nimbleIdentityMigrationObject.m_newAuthenticatorId, nimbleIdentityMigrationObject.m_newAuthenticatorPid, nimbleIdentityMigrationObject.m_currentAuthenticatorId, nimbleIdentityMigrationObject.m_currentAuthenticatorPid);
        Log.Helper.LOGI(this, "Sending request to game to handle pending migration", new Object[0]);
        this.m_conductor.handlePendingMigration(nimbleIdentityPendingMigrationResolver);
        return true;
    }
}
