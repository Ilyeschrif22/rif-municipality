import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@/components/ui/sidebar';
import { NavGroup } from '@/components/layout/nav-group';
import { NavUser } from '@/components/layout/nav-user';
import { TeamSwitcher } from '@/components/layout/team-switcher';
import { sidebarData } from './data/sidebar-data';
import { useAuthStore } from '@/stores/authStore';
import { useEffect } from 'react';
import { api } from '@/lib/api';

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { setUser } = useAuthStore((s) => s.auth); 

  useEffect(() => {
    const fetchUserRole = async () => {
      try {
        const response = await api.get('/api/account');
        const userData = response.data as any;
        const role = Array.isArray(userData?.authorities) ? userData.authorities : [];
        setUser({
          accountNo: userData?.login ?? '',
          email: userData?.email ?? '',
          firstName: userData?.firstName ?? '',
          lastName: userData?.lastName ?? '',
          role: role,
          exp: 0,
        });
        console.log('User role:', role);
        console.log(sidebarData)
      } catch (error) {
        console.error('Failed to fetch user role:', error);
      }
    };

    fetchUserRole();
  }, [setUser]);

  return (
    <Sidebar collapsible="icon" variant="floating" {...props}>
      <SidebarContent>
        {sidebarData.navGroups.map((props) => (
          <NavGroup key={props.title} {...props} />
        ))}
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={sidebarData.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}