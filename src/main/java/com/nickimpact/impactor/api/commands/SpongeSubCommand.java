package com.nickimpact.impactor.api.commands;

import com.nickimpact.impactor.api.plugins.SpongePlugin;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public abstract class SpongeSubCommand extends SpongeCommand {

	public SpongeSubCommand(SpongePlugin plugin) {
		super(plugin);
	}

	/**
	 * Inherited by {@link SpongeCommand}, and set to do nothing to help
	 * ensure we don't register the same command twice, once as a subcommand,
	 * and once as it's own thing.
	 */
	@Override
	public void register(SpongePlugin plugin) {
		// Command registry is dropped for sub-commands
	}
}
